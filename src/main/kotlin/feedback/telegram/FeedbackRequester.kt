package feedback.telegram

import common.telegram.MassSendLimiter
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import dev.inmo.tgbotapi.types.toChatId
import feedback.domain.usecases.GetFeedbackRequestsFlowUseCase
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
                text = Strings.feedbackRequest(feedbackRequest.respondentName),
                replyMarkup = flatInlineKeyboard {
                    TODO()
                }
            )
            massSendLimiter.wait()
        }
    }
}
