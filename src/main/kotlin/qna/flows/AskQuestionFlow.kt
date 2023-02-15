package qna.flows

import auth.domain.entities.User
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
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import menus.states.MenuState
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent
import qna.states.*
import qna.strings.ButtonStrings
import qna.strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.askQuestionFlow() {
    state<MenuState.Questions.AskQuestion> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Question.WordingQuestion
            )
        }
        onText { message ->
            val question = message.content.text
            state.override { ChooseQuestionAreas(question, emptySet()) }
        }
    }
    state<ChooseQuestionAreas> {
        onEnter {
            //множественный выбор из областей (inline кнопки)
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
                        question,
                        areas,
                        messageId
                    )
                }
            } else {
                state.override {
                    ChooseQuestionIntent(
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
            //цель вопроса (3 кнопки)
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
        //написала по-тупому, потом переделаю
        onText { message ->
            if (message.equals(QuestionIntent.FreeForm)) {
                state.override {
                    SendQuestionToCommunity(question, areas, QuestionIntent.FreeForm)
                }
            } else if (message.equals(QuestionIntent.Consultation)) {
                    state.override {
                        SendQuestionToCommunity(question, areas, QuestionIntent.Consultation)
                    }
                }
            else if (message.equals(QuestionIntent.TestHypothesis)) {
                state.override {
                    SendQuestionToCommunity(question, areas, QuestionIntent.TestHypothesis)
                }
            } else {
                //TODO: сообщение, что необходимо выбрать из кнопочного меню
                state.override { ChooseQuestionIntent(question, areas) }
            }
        }
    }
    state<SendQuestionToCommunity> {
        onEnter {
            //TODO: придумать текст
            sendTextMessage(
                it,
                "Отлично, вопрос сформирован!",
                replyMarkup = replyKeyboard {
                    row{
                        simpleButton(ButtonStrings.SendQuestion)
                    }
                }
            )
        }
        onText(ButtonStrings.SendQuestion){
            //добавление вопроса в бд
            //сообщение о том, что вопрос успешно отправлен
            //отправка вопроса в сообщество
            //возвращаемся в состояние меню(DialogState.Empty)
            state.override { DialogState.Empty }
        }
    }
    //еще один state для отправки сообщения в сообщество
}