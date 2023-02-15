package feedback.telegram.flows

import auth.domain.entities.User
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.utils.asMessageCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.withContent
import dev.inmo.tgbotapi.types.message.content.TextContent
import feedback.domain.usecases.SetFeedbackUseCase
import feedback.telegram.Strings
import feedback.telegram.queries.FeedbackQuery
import generated.RoleFilterBuilder
import generated.onDataCallbackQuery
import org.koin.core.component.inject

fun RoleFilterBuilder<User.Normal>.feedbackFlow() {
    val setFeedback: SetFeedbackUseCase by inject()
    anyState {
        onDataCallbackQuery(FeedbackQuery.SendFeedback::class) { (data, query) ->
            val result = setFeedback(query.from.id.chatId, data.responseId, data.isSuccessful)
            if (result == SetFeedbackUseCase.Result.OK) {
                val message = query.asMessageCallbackQuery()?.message
                    ?.withContent<TextContent>() ?: return@onDataCallbackQuery
                val text = if (data.isSuccessful) Strings.IfSuccessful else Strings.IfUnsuccessful
                edit(message, text = text, replyMarkup = null)
            }
            answer(query)
        }
    }
}
