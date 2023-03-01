package common.telegram.functions

import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.queries.FinishQuestionQuery
import auth.telegram.queries.SelectQuestionQuery
import auth.telegram.queries.UnselectQuestionQuery
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import qna.domain.entities.QuestionArea

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
                    val areaToString = Strings.questionAreaToString[area]
                    if (area in getAreas(state.snapshot)) {
                        dataButton("✅$areaToString", UnselectQuestionQuery(area))
                    } else {
                        dataButton(areaToString!!, SelectQuestionQuery(area))
                    }
                }
            }
            row {
                dataButton(Strings.FinishChoosing, FinishQuestionQuery)
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
            state.overrideQuietly { onMessageIdSet(state.snapshot, message.messageId) }
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
                Strings.AccountInfo.NoQuestionArea
            )
        } else {
            state.override { onFinish(state.snapshot) }
        }
        answer(query)
    }
}
