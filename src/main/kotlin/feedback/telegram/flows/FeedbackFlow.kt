package feedback.telegram.flows

import auth.domain.entities.User
import common.telegram.ButtonStrings
import common.telegram.deleteAfterDelay
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrNull
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContent
import dev.inmo.tgbotapi.types.message.content.TextContent
import feedback.domain.usecases.CloseQuestionUseCase
import feedback.domain.usecases.GetAssociatedOpenQuestionUseCase
import feedback.domain.usecases.SetFeedbackUseCase
import feedback.telegram.Strings
import feedback.telegram.queries.FeedbackQueries
import generated.RoleFilterBuilder
import generated.dataButton
import generated.onDataCallbackQuery
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.inject

fun RoleFilterBuilder<User.Normal>.feedbackFlow() {
    val setFeedback: SetFeedbackUseCase by inject()
    val getAssociatedOpenQuestion: GetAssociatedOpenQuestionUseCase by inject()
    val closeQuestion: CloseQuestionUseCase by inject()
    anyState {
        onDataCallbackQuery(FeedbackQueries.SendFeedback::class) { (data, query) ->
            val result = setFeedback(query.from.id.chatId, data.responseId, data.isSuccessful)
            if (result != SetFeedbackUseCase.Result.OK) return@onDataCallbackQuery
            val message = query.messageCallbackQueryOrNull()?.message
                ?.withContent<TextContent>() ?: return@onDataCallbackQuery
            if (data.isSuccessful) {
                val question = getAssociatedOpenQuestion(query.from.id.chatId, data.responseId)
                if (question != null) {
                    val keyboard = flatInlineKeyboard {
                        dataButton(ButtonStrings.No, FeedbackQueries.DoNotCloseQuestion)
                        dataButton(ButtonStrings.Yes, FeedbackQueries.CloseQuestion(question.id))
                    }
                    edit(message, text = Strings.shouldCloseQuestion(question.subject), replyMarkup = keyboard)
                } else {
                    edit(message, text = Strings.IfSuccessful, replyMarkup = null)
                    deleteAfterDelay(message)
                }
            } else {
                edit(message, text = Strings.IfUnsuccessful, replyMarkup = null)
                deleteAfterDelay(message)
            }
            answer(query)
        }
        onDataCallbackQuery(FeedbackQueries.CloseQuestion::class) { (data, query) ->
            val result = closeQuestion(fromUserId = query.from.id.chatId, questionId = data.questionId)
            if (result != CloseQuestionUseCase.Result.OK) return@onDataCallbackQuery
            val message = query.messageCallbackQueryOrNull()?.message
                ?.withContent<TextContent>() ?: return@onDataCallbackQuery
            edit(message, text = Strings.QuestionClosed, replyMarkup = null)
            deleteAfterDelay(message)
            answer(query)
        }
        onDataCallbackQuery(FeedbackQueries.DoNotCloseQuestion::class) { (_, query) ->
            val message = query.messageCallbackQueryOrNull()?.message
                ?.withContent<TextContent>() ?: return@onDataCallbackQuery
            edit(message, text = Strings.QuestionNotClosed, replyMarkup = null)
            deleteAfterDelay(message)
            answer(query)
        }
    }
}
