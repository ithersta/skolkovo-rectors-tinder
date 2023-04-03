package qna.telegram

import common.telegram.MassSendLimiter
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import org.koin.core.annotation.Single
import qna.domain.usecases.GetNewQuestionNotificationFlowUseCase
import qna.telegram.flows.sendQuestionMessage

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
            }
        }
    }
}
