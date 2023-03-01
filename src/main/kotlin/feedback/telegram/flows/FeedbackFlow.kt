package feedback.telegram.flows

import auth.domain.entities.User
import common.telegram.confirmationInlineKeyboard
import common.telegram.deleteAfterDelay
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrNull
import dev.inmo.tgbotapi.extensions.utils.withContentOrNull
import dev.inmo.tgbotapi.types.message.content.TextContent
import feedback.domain.usecases.CloseQuestionUseCase
import feedback.domain.usecases.GetAssociatedOpenQuestionUseCase
import feedback.domain.usecases.SetFeedbackUseCase
import feedback.telegram.Strings
import feedback.telegram.queries.FeedbackQueries
import generated.RoleFilterBuilder
import generated.onDataCallbackQuery
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
                ?.withContentOrNull<TextContent>() ?: return@onDataCallbackQuery
            val question = getAssociatedOpenQuestion(query.from.id.chatId, data.responseId)
            if (data.isSuccessful && question != null) {
                checkNotNull(question.id)
                val keyboard = confirmationInlineKeyboard(
                    positiveData = FeedbackQueries.CloseQuestion(question.id),
                    negativeData = FeedbackQueries.DoNotCloseQuestion
                )
                edit(message, text = Strings.shouldCloseQuestion(question.subject), replyMarkup = keyboard)
            } else {
                val text = if (data.isSuccessful) Strings.IfSuccessful else Strings.IfUnsuccessful
                edit(message, text = text, replyMarkup = null)
                deleteAfterDelay(message)
            }
            answer(query)
        }
        onDataCallbackQuery(FeedbackQueries.CloseQuestion::class) { (data, query) ->
            val result = closeQuestion(fromUserId = query.from.id.chatId, questionId = data.questionId)
            if (result != CloseQuestionUseCase.Result.OK) return@onDataCallbackQuery
            val message = query.messageCallbackQueryOrNull()?.message
                ?.withContentOrNull<TextContent>() ?: return@onDataCallbackQuery
            edit(message, text = Strings.QuestionClosed, replyMarkup = null)
            deleteAfterDelay(message)
            answer(query)
        }
        onDataCallbackQuery(FeedbackQueries.DoNotCloseQuestion::class) { (_, query) ->
            val message = query.messageCallbackQueryOrNull()?.message
                ?.withContentOrNull<TextContent>() ?: return@onDataCallbackQuery
            edit(message, text = Strings.QuestionNotClosed, replyMarkup = null)
            deleteAfterDelay(message)
            answer(query)
        }
    }
}
