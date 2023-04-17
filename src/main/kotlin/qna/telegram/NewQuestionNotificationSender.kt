package qna.telegram

import common.telegram.MassSendLimiter
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import io.github.oshai.KotlinLogging
import org.koin.core.annotation.Single
import qna.domain.usecases.GetNewQuestionNotificationFlowUseCase
import qna.telegram.flows.sendQuestionMessage

private val logger = KotlinLogging.logger {}

@Single
class NewQuestionNotificationSender(
    private val getNewQuestionNotificationFlow: GetNewQuestionNotificationFlowUseCase,
    private val massSendLimiter: MassSendLimiter
) {
    context(BehaviourContext)
    fun setup() = launchSafelyWithoutExceptions {
        getNewQuestionNotificationFlow().collect { notification ->
            massSendLimiter.wait()
            runCatching {
                sendQuestionMessage(notification.userId.toChatId(), notification.question)
            }.onFailure { exception ->
                logger.error("Couldn't send new question notification to ${notification.userId}", exception)
            }
        }
    }
}
