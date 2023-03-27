package notifications.telegram

import common.telegram.MassSendLimiter
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import notifications.domain.usecases.GetDelayedQuestionsNotificationFlowUseCase
import notifications.telegram.flows.newQuestionsPager
import org.koin.core.annotation.Single

@Single
class DelayedQuestionsNotificationSender(
    private val getNewQuestionsNotificationFlow: GetDelayedQuestionsNotificationFlowUseCase,
    private val massSendLimiter: MassSendLimiter
) {
    fun BehaviourContext.setup() = launchSafelyWithoutExceptions {
        getNewQuestionsNotificationFlow().collect { notification ->
            massSendLimiter.wait()
            runCatching {
                val replyMarkup = newQuestionsPager.replyMarkup(notification, context = null)
                if (replyMarkup.keyboard.isNotEmpty()) {
                    sendTextMessage(
                        notification.userId.toChatId(),
                        text = Strings.newQuestionsMessage(notification.notificationPreference),
                        replyMarkup = replyMarkup
                    )
                }
            }
        }
    }
}
