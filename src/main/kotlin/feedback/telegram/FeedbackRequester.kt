package feedback.telegram

import common.telegram.MassSendLimiter
import common.telegram.functions.confirmationInlineKeyboard
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import feedback.domain.usecases.GetFeedbackRequestsFlowUseCase
import feedback.telegram.queries.FeedbackQueries
import io.github.oshai.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class FeedbackRequester(
    private val getFeedbackRequestsFlow: GetFeedbackRequestsFlowUseCase,
    private val massSendLimiter: MassSendLimiter
) {
    fun BehaviourContext.setup() = launchSafelyWithoutExceptions {
        getFeedbackRequestsFlow().collect { feedbackRequest ->
            massSendLimiter.wait()
            runCatching {
                send(
                    chatId = feedbackRequest.questionAuthorUserId.toChatId(),
                    entities = Strings.feedbackRequest(feedbackRequest),
                    replyMarkup = confirmationInlineKeyboard(
                        positiveData = FeedbackQueries.SendFeedback(feedbackRequest.responseId, true),
                        negativeData = FeedbackQueries.SendFeedback(feedbackRequest.responseId, false)
                    )
                )
            }.onFailure { exception ->
                logger.error("Couldn't request feedback from ${feedbackRequest.questionAuthorUserId}", exception)
            }
        }
    }
}
