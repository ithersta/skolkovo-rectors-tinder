package feedback.telegram

import common.telegram.MassSendLimiter
import common.telegram.functions.confirmationInlineKeyboard
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import feedback.domain.usecases.GetFeedbackRequestsFlowUseCase
import feedback.telegram.queries.FeedbackQueries
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class FeedbackRequester(
    private val getFeedbackRequestsFlow: GetFeedbackRequestsFlowUseCase,
    private val massSendLimiter: MassSendLimiter
) {
    fun BehaviourContext.setup() = launch {
        getFeedbackRequestsFlow().collect { feedbackRequest ->
            send(
                chatId = feedbackRequest.questionAuthorUserId.toChatId(),
                entities = Strings.feedbackRequest(feedbackRequest),
                replyMarkup = confirmationInlineKeyboard(
                    positiveData = FeedbackQueries.SendFeedback(feedbackRequest.responseId, true),
                    negativeData = FeedbackQueries.SendFeedback(feedbackRequest.responseId, false)
                )
            )
            massSendLimiter.wait()
        }
    }
}
