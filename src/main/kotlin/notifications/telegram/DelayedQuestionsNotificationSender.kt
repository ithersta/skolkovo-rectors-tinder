package notifications.telegram

import common.telegram.MassSendLimiter
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import io.github.oshai.KotlinLogging
import notifications.domain.usecases.GetDelayedQuestionsNotificationFlowUseCase
import org.koin.core.annotation.Single
import qna.telegram.flows.QuestionDigestPagerData
import qna.telegram.flows.questionDigestPager

private val logger = KotlinLogging.logger {}

@Single
class DelayedQuestionsNotificationSender(
    private val getNewQuestionsNotificationFlow: GetDelayedQuestionsNotificationFlowUseCase,
    private val massSendLimiter: MassSendLimiter
) {
    fun BehaviourContext.setup() = launchSafelyWithoutExceptions {
        getNewQuestionsNotificationFlow().collect { notification ->
            massSendLimiter.wait()
            runCatching {
                val replyMarkup = questionDigestPager.replyMarkup(
                    data = QuestionDigestPagerData(
                        userId = notification.userId,
                        from = notification.from,
                        until = notification.until,
                        area = null,
                        notificationPreference = notification.notificationPreference
                    ),
                    context = null
                )
                if (replyMarkup.keyboard.isNotEmpty()) {
                    sendTextMessage(
                        notification.userId.toChatId(),
                        text = Strings.newQuestionsMessage(notification.notificationPreference),
                        replyMarkup = replyMarkup
                    )
                }
            }.onFailure { exception ->
                logger.error("Couldn't send question digest to ${notification.userId}", exception)
            }
        }
    }
}
