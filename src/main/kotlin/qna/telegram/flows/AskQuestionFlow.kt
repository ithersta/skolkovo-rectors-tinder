package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.StatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.MassSendLimiter
import common.telegram.functions.chooseQuestionAreas
import common.telegram.functions.confirmationInlineKeyboard
import dev.inmo.tgbotapi.extensions.api.answers.answer
import config.BotConfig
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.sendContact
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrNull
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.extensions.utils.withContentOrNull
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.onDataCallbackQuery
import kotlinx.coroutines.launch
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.entities.Question
import qna.domain.entities.QuestionIntent
import qna.domain.usecases.*
import qna.telegram.queries.AcceptQuestionQuery
import qna.telegram.queries.DeclineQuestionQuery
import qna.telegram.states.*
import qna.telegram.states.AskFullQuestion
import qna.telegram.states.ChooseQuestionAreas
import qna.telegram.states.ChooseQuestionIntent
import qna.telegram.states.SendQuestionToCommunity
import qna.telegram.strings.ButtonStrings
import qna.telegram.strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.askQuestionFlow() {
    val getUsersByAreaUseCase: GetUsersByAreaUseCase by inject()
    val getFilteredUsersByAreaUseCase: GetFilteredUsersByAreaUseCase by inject()
    val addQuestionUseCase: AddQuestionUseCase by inject()
    val getQuestionByIdUseCase: GetQuestionByIdUseCase by inject()
    val addResponseUseCase: AddResponseUseCase by inject()
    val botConfig: BotConfig by inject()
    val massSendLimiter: MassSendLimiter by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    state<MenuState.Questions.AskQuestion> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Question.SubjectQuestion,
                replyMarkup = ReplyKeyboardRemove()
            )
        }
        onText { message ->
            state.override { AskFullQuestion(message.content.text) }
        }
    }
    state<AskFullQuestion> {
        onEnter {
            sendTextMessage(it, Strings.Question.WordingQuestion)
        }
        onText { message ->
            state.override { ChooseQuestionAreas(subject, message.content.text, emptySet()) }
        }
    }
    state<ChooseQuestionAreas> {
        chooseQuestionAreas(
            text = auth.telegram.Strings.Question.ChooseQuestionArea,
            getAreas = { it.areas },
            getMessageId = { it.messageId },
            onSelectionChanged = { state, areas -> state.copy(areas = areas) },
            onMessageIdSet = { state, messageId -> state.copy(messageId = messageId) },
            onFinish = { ChooseQuestionIntent(it.subject, it.question, it.areas) }
        )
    }
    state<ChooseQuestionIntent> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Question.AskingQuestionIntent,
                replyMarkup = replyKeyboard {
                    ButtonStrings.Question.questionIntentToString.forEach {
                        row {
                            simpleButton(it.value)
                        }
                    }
                }
            )
        }
        onText { message ->
            val intent = ButtonStrings.Question.stringToQuestionIntent[message.content.text]
            if (intent != null) {
                if (intent == QuestionIntent.QuestionToColleagues) {
                    state.override { SendQuestionToCurator(subject, question) }
                } else {
                    state.override { SendQuestionToCommunity(subject, question, areas, intent) }
                }
            } else {
                sendTextMessage(message.chat, Strings.Question.InvalidQuestionIntent)
                state.override { ChooseQuestionIntent(subject, question, areas) }
            }
        }
    }
    state<SendQuestionToCommunity> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Question.CompletedQuestion,
                replyMarkup = replyKeyboard(resizeKeyboard = true) {
                    row {
                        simpleButton(ButtonStrings.SendQuestionWithRestrictions)
                    }
                    row{
                        simpleButton(ButtonStrings.SendQuestion)
                    }
                }
            )
        }
        onText(ButtonStrings.SendQuestion) { message ->
            val question = addQuestionUseCase(
                authorId = message.chat.id.chatId,
                state.snapshot.intent,
                state.snapshot.subject,
                state.snapshot.question,
                state.snapshot.areas,
                false
            )
            sendTextMessage(message.chat, Strings.Question.Success)
            coroutineScope.launch {
                state.snapshot.areas.flatMap {
                    getUsersByAreaUseCase(it, userId = message.chat.id.chatId)
                }.toSet().forEach {
                    runCatching {
                        massSendLimiter.wait()
                        sendQuestionMessage(it.toChatId(), question)
                    }
                }
            }
            state.override { DialogState.Empty }
        }
        onText(ButtonStrings.SendQuestionWithRestrictions) { message ->
            val question = addQuestionUseCase(
                authorId = message.chat.id.chatId,
                state.snapshot.intent,
                state.snapshot.subject,
                state.snapshot.question,
                state.snapshot.areas,
                true
            )
            sendTextMessage(message.chat, Strings.Question.Success)
            coroutineScope.launch {
                val user = getUserDetailsUseCase(message.chat.id.chatId)!!
                state.snapshot.areas.flatMap {
                    getFilteredUsersByAreaUseCase(it, user)
                }.toSet().forEach {
                    runCatching {
                        massSendLimiter.wait()
                        sendQuestionMessage(it.toChatId(), question)
                    }
                }
            }
            state.override { DialogState.Empty }
        }
    }
    state<SendQuestionToCurator> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Question.CompletedQuestion,
                replyMarkup = replyKeyboard(resizeKeyboard = true) {
                    row {
                        simpleButton(ButtonStrings.SendQuestionCenter)
                    }
                }
            )
        }
        onText(ButtonStrings.SendQuestionCenter) { message ->
            sendTextMessage(
                message.chat,
                Strings.Question.Success
            )
            sendTextMessage(
                UserId(botConfig.curatorId),
                Strings.QuestionToCurator.message(state.snapshot.subject, state.snapshot.question)
            )
            val userDetails = getUserDetailsUseCase(message.chat.id.chatId)!!
            sendContact(
                UserId(botConfig.curatorId),
                phoneNumber = userDetails.phoneNumber.value,
                firstName = userDetails.name,
            )
            state.override { DialogState.Empty }
        }
    }
    anyState {
        onDataCallbackQuery(DeclineQuestionQuery::class) { (_, query) ->
            val message = query.messageCallbackQueryOrNull()?.message ?: return@onDataCallbackQuery
            delete(message)
            answer(query)
        }
        onDataCallbackQuery(AcceptQuestionQuery::class) { (data, query) ->
            val question = getQuestionByIdUseCase(data.questionId)
            val message = query.messageCallbackQueryOrNull()?.message?.withContentOrNull<TextContent>()
            message?.let {
                edit(
                    it,
                    entities = Strings.ToAnswerUser.editMessage(question!!.subject, question.text),
                    replyMarkup = null
                )
            }
            addResponseUseCase(data.questionId, query.user.id.chatId)
            sendTextMessage(
                query.user.id,
                Strings.ToAnswerUser.SentAgreement
            )
            answer(query)
        }
    }
}

suspend fun TelegramBot.sendQuestionMessage(
    chatId: ChatId,
    question: Question
) = sendTextMessage(
    chatId,
    Strings.ToAnswerUser.message(question),
    replyMarkup = confirmationInlineKeyboard(
        positiveData = AcceptQuestionQuery(question.id!!),
        negativeData = DeclineQuestionQuery
    )
)
