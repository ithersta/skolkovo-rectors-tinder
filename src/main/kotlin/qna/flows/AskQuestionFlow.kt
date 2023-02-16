package qna.flows

import auth.domain.entities.User
import auth.domain.usecases.GetUsersByAreaUseCase
import auth.telegram.queries.FinishQuestionQuery
import auth.telegram.queries.SelectQuestionQuery
import auth.telegram.queries.UnselectQuestionQuery
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import kotlinx.coroutines.launch
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent
import qna.states.*
import qna.strings.ButtonStrings
import qna.strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.askQuestionFlow() {
    val getUsersByAreaUseCase: GetUsersByAreaUseCase by inject()

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
        onEnter {
            val keyboard = inlineKeyboard {
                QuestionArea.values().forEach { area ->
                    row {
                        val areaToString = auth.telegram.Strings.questionAreaToString[area]
                        if (area in state.snapshot.areas) {
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
            state.snapshot.messageId?.let { id ->
                runCatching {
                    editMessageReplyMarkup(it, id, keyboard)
                }
            } ?: run {
                val message = sendTextMessage(
                    it,
                    auth.telegram.Strings.Question.ChooseQuestionArea,
                    replyMarkup = keyboard
                )
                state.overrideQuietly {
                    ChooseQuestionAreas(
                        subject,
                        question,
                        areas,
                        message.messageId
                    )
                }
            }
        }
        onDataCallbackQuery(SelectQuestionQuery::class) { (data, query) ->
            state.override {
                ChooseQuestionAreas(
                    subject,
                    question,
                    areas + data.area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(UnselectQuestionQuery::class) { (data, query) ->
            state.override {
                ChooseQuestionAreas(
                    subject,
                    question,
                    areas - data.area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(FinishQuestionQuery::class) { (_, query) ->
            if (state.snapshot.areas.isEmpty()) {
                sendTextMessage(
                    query.user.id,
                    auth.telegram.Strings.AccountInfo.NoQuestionArea
                )
                state.override {
                    ChooseQuestionAreas(
                        subject,
                        question,
                        areas,
                        messageId
                    )
                }
            } else {
                state.override {
                    ChooseQuestionIntent(
                        subject,
                        question,
                        areas
                    )
                }
            }
            answer(query)
        }
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
            when (message.content.text) {
                Strings.Question.Intent.FreeForm -> {
                    state.override {
                        SendQuestionToCommunity(subject, question, areas, QuestionIntent.FreeForm)
                    }
                }

                Strings.Question.Intent.Consultation -> {
                    state.override {
                        SendQuestionToCommunity(subject, question, areas, QuestionIntent.Consultation)
                    }
                }

                Strings.Question.Intent.TestHypothesis -> {
                    state.override {
                        SendQuestionToCommunity(subject, question, areas, QuestionIntent.TestHypothesis)
                    }
                }

                else -> {
                    // TODO: сообщение, что необходимо выбрать из кнопочного меню
                    // надо придумать, как лучше будет отправлять
                    sendTextMessage(message.chat, Strings.Question.InvalidQuestionIntent)
                    state.override { ChooseQuestionIntent(subject, question, areas) }
                }
            }
        }
    }
    state<SendQuestionToCommunity> {
        onEnter {
            // TODO: придумать текст
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
            // TODO добавление вопроса в бд

            sendTextMessage(
                message.chat,
                Strings.Question.Success
            )
            // TODO: не отправляется с inline, но отправляется с reply
            coroutineScope.launch {
                val listOfValidUsers: List<Long> =
                    getUsersByAreaUseCase(
                        QuestionArea.Education, // заменить на areas из state (forEach и тд)
                        userId = message.chat.id.chatId
                    )
                listOfValidUsers.forEach {
                    runCatching {
                        sendTextMessage(
                            it.toChatId(),
                            Strings.ToAnswerUser.message(state.snapshot.subject, state.snapshot.question),
                            replyMarkup = inlineKeyboard {
                                row {
                                    dataButton(
                                        ButtonStrings.Option.Yes,
                                        "Да"
                                    ) // вот тут я не совсем понимаю, как работает
                                    // мб из-за этого не отправляет
                                }
                                row {
                                    dataButton(ButtonStrings.Option.No, "Нет")
                                }
                            }
                        )
                    }
                }
            }
            state.override { DialogState.Empty }
        }
    }
}
