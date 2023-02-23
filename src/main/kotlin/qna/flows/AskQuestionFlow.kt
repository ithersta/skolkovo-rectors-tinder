package qna.flows

import auth.domain.entities.User
import auth.telegram.queries.FinishQuestionQuery
import auth.telegram.queries.SelectQuestionQuery
import auth.telegram.queries.UnselectQuestionQuery
import com.ithersta.tgbotapi.fsm.StatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendContact
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrNull
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import kotlinx.coroutines.launch
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent
import qna.domain.usecases.*
import qna.states.*
import qna.strings.ButtonStrings
import qna.strings.Strings
import qna.telegram.queries.AcceptQuestionQuery
import qna.telegram.queries.AcceptUserQuery
import qna.telegram.queries.DeclineQuestionQuery
import qna.telegram.queries.DeclineUserQuery

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.askQuestionFlow() {
    val getUsersByAreaUseCase: GetUsersByAreaUseCase by inject()
    val addQuestionUseCase: AddQuestionUseCase by inject()
    val getUserIdUseCase: GetUserIdUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val getQuestionTextByIdUseCase: GetQuestionTextByIdUseCase by inject()
    val getPhoneNumberUseCase: GetPhoneNumberUseCase by inject()
    state<MenuState.Questions.AskQuestion> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Question.SubjectQuestion,
                replyMarkup = ReplyKeyboardRemove()
            )
        }
        onText { message ->
            val subject = message.content.text
            state.override { AskFullQuestion(subject) }
        }
    }
    state<AskFullQuestion> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Question.WordingQuestion
            )
        }
        onText { message ->
            val question = message.content.text
            state.override { ChooseQuestionAreas(subject, question, emptySet()) }
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
                    row {
                        simpleButton(Strings.Question.Intent.TestHypothesis)
                    }
                    row {
                        simpleButton(Strings.Question.Intent.Consultation)
                    }
                    row {
                        simpleButton(Strings.Question.Intent.FreeForm)
                    }
                }
            )
        }
        onText { message ->
            val intent = Strings.Question.stringToQuestionIntent[message.content.text]
            if (intent != null) {
                state.override { SendQuestionToCommunity(subject, question, areas, intent) }
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
                replyMarkup = replyKeyboard {
                    row {
                        simpleButton(ButtonStrings.SendQuestion)
                    }
                }
            )
        }
        onText(ButtonStrings.SendQuestion) { message ->
            val details = Question(
                message.chat.id.chatId,
                state.snapshot.intent,
                state.snapshot.subject,
                state.snapshot.question,
                false,
                state.snapshot.areas
            )
            val question = addQuestionUseCase(details)
            sendTextMessage(
                message.chat,
                Strings.Question.Success
            )
            coroutineScope.launch {
                state.snapshot.areas.forEach {
                    val listOfValidUsers: List<Long> =
                        getUsersByAreaUseCase(
                            it,
                            userId = message.chat.id.chatId
                        )
                    listOfValidUsers.forEach {
                        runCatching {
                            sendQuestionMessage(it.toChatId(), question)
                        }
                    }
                }
            }
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
            sendTextMessage(
                query.user.id,
                Strings.ToAnswerUser.SentAgreement
            )
            coroutineScope.launch {
                val userId = getUserIdUseCase(data.questionId)
                val user = getUserDetailsUseCase(userId)
                if (user != null) {
                    sendTextMessage(
                        userId.toChatId(),
                        Strings.ToAskUser.message(
                            user.name,
                            user.city,
                            user.job,
                            user.organization,
                            user.activityDescription
                        ),
                        replyMarkup = inlineKeyboard {
                            row {
                                dataButton(
                                    ButtonStrings.Option.Yes,
                                    AcceptUserQuery(query.user.id.chatId, data.questionId)
                                )
                            }
                            row {
                                dataButton(
                                    ButtonStrings.Option.No,
                                    DeclineUserQuery(query.user.id.chatId)
                                )
                            }
                        }
                    )
                    sendContact(
                        userId.toChatId(),
                        phoneNumber = getPhoneNumberUseCase(userId),
                        firstName = user.name,
                    )
                }
            }
            answer(query)
        }
        onDataCallbackQuery(DeclineUserQuery::class) { (data, query) ->
            sendTextMessage(
                data.userId.toChatId(),
                Strings.ToAnswerUser.QuestionResolved
            )
            answer(query)
        }
        onDataCallbackQuery(AcceptUserQuery::class) { (data, query) ->
            sendTextMessage(
                data.userId.toChatId(),
                Strings.ToAnswerUser.WaitingForCompanion
            )
            sendTextMessage(
                query.user.id,
                Strings.ToAskUser.WriteToCompanion
            )
            sendTextMessage(
                query.user.id,
                getQuestionTextByIdUseCase(data.questionId)
            )
            sendTextMessage(
                query.user.id,
                Strings.ToAskUser.CopyQuestion
            )
            answer(query)
        }
    }
}

fun <State : DialogState> StateFilterBuilder<DialogState, User, State, *, UserId>.chooseQuestionAreas(
    text: String,
    getAreas: (State) -> Set<QuestionArea>,
    getMessageId: (State) -> MessageId?,
    onSelectionChanged: (State, Set<QuestionArea>) -> State,
    onMessageIdSet: (State, MessageId) -> State,
    onFinish: (State) -> DialogState
) {
    onEnter {
        val keyboard = inlineKeyboard {
            QuestionArea.values().forEach { area ->
                row {
                    val areaToString = auth.telegram.Strings.questionAreaToString[area]
                    if (area in getAreas(state.snapshot)) {
                        dataButton("✅$areaToString", UnselectQuestionQuery(area))
                    } else {
                        dataButton(areaToString!!, SelectQuestionQuery(area))
                    }
                }
            }
            row {
                dataButton(auth.telegram.Strings.FinishChoosing, FinishQuestionQuery)
            }
        }
        getMessageId(state.snapshot)?.let { id ->
            runCatching {
                editMessageReplyMarkup(it, id, keyboard)
            }
        } ?: run {
            val message = sendTextMessage(
                it,
                text,
                replyMarkup = keyboard
            )
            state.overrideQuietly {
                onMessageIdSet(state.snapshot, message.messageId)
            }
        }
    }
    onDataCallbackQuery(SelectQuestionQuery::class) { (data, query) ->
        state.override {
            onSelectionChanged(state.snapshot, getAreas(state.snapshot) + data.area)
        }
        answer(query)
    }
    onDataCallbackQuery(UnselectQuestionQuery::class) { (data, query) ->
        state.override {
            onSelectionChanged(state.snapshot, getAreas(state.snapshot) - data.area)
        }
        answer(query)
    }
    onDataCallbackQuery(FinishQuestionQuery::class) { (_, query) ->
        if (getAreas(state.snapshot).isEmpty()) {
            sendTextMessage(
                query.user.id,
                auth.telegram.Strings.AccountInfo.NoQuestionArea
            )
        } else {
            state.override {
                onFinish(state.snapshot)
            }
        }
        answer(query)
    }
}

suspend fun StatefulContext<DialogState, User, SendQuestionToCommunity, User.Normal>.sendQuestionMessage(
    chatId: ChatId,
    question: Question
) = sendTextMessage(
    chatId,
    Strings.ToAnswerUser.message(question.subject, question.text),
    replyMarkup = inlineKeyboard {
        row {
            checkNotNull(question.id)
            dataButton(
                ButtonStrings.Option.Yes,
                AcceptQuestionQuery(question.id)
            )
        }
        row {
            dataButton(
                ButtonStrings.Option.No,
                DeclineQuestionQuery
            )
        }
    }
)
