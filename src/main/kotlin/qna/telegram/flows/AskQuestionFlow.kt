package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.functions.chooseQuestionAreas
import common.telegram.functions.confirmationInlineKeyboard
import config.BotConfig
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.sendContact
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrNull
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.extensions.utils.withContentOrNull
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import generated.onDataCallbackQuery
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.entities.HideFrom
import qna.domain.entities.Question
import qna.domain.entities.QuestionIntent
import qna.domain.usecases.AddQuestionUseCase
import qna.domain.usecases.AddResponseUseCase
import qna.domain.usecases.GetQuestionByIdUseCase
import qna.domain.usecases.GetUserDetailsUseCase
import qna.telegram.queries.AcceptQuestionQuery
import qna.telegram.queries.DeclineQuestionQuery
import qna.telegram.states.*
import qna.telegram.strings.ButtonStrings
import qna.telegram.strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.askQuestionFlow() {
    val addQuestionUseCase: AddQuestionUseCase by inject()
    val getQuestionByIdUseCase: GetQuestionByIdUseCase by inject()
    val addResponseUseCase: AddResponseUseCase by inject()
    val botConfig: BotConfig by inject()
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
                    row { simpleButton(ButtonStrings.SendQuestion.ExcludeMyOrganization) }
                    row { simpleButton(ButtonStrings.SendQuestion.ExcludeMyCity) }
                    row { simpleButton(ButtonStrings.SendQuestion.ToAll) }
                }
            )
        }
        onText(
            ButtonStrings.SendQuestion.ExcludeMyOrganization,
            ButtonStrings.SendQuestion.ExcludeMyCity,
            ButtonStrings.SendQuestion.ToAll
        ) { message ->
            val hideFrom = when (message.content.text) {
                ButtonStrings.SendQuestion.ExcludeMyOrganization -> HideFrom.SameOrganization
                ButtonStrings.SendQuestion.ExcludeMyCity -> HideFrom.SameCity
                ButtonStrings.SendQuestion.ToAll -> HideFrom.NoOne
                else -> error("Other messages should be filtered")
            }
            addQuestionUseCase(
                authorId = message.chat.id.chatId,
                intent = state.snapshot.intent,
                subject = state.snapshot.subject,
                text = state.snapshot.question,
                areas = state.snapshot.areas,
                hideFrom = hideFrom
            )
            sendTextMessage(message.chat, Strings.Question.Success)
            state.override { DialogState.Empty }
        }
    }
    state<SendQuestionToCurator> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Question.CompletedQuestion,
                replyMarkup = flatReplyKeyboard(resizeKeyboard = true) {
                    simpleButton(ButtonStrings.SendQuestion.ToCenter)
                }
            )
        }
        onText(ButtonStrings.SendQuestion.ToCenter) { message ->
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
                firstName = userDetails.name
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
