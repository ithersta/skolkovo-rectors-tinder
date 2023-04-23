package event.telegram

import common.telegram.MassSendLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import event.telegram.flows.GetNewEventNotificationFlowUseCase
import event.telegram.flows.sendEventMessage
import io.github.oshai.KotlinLogging
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class NewEventNotificationSender(
    private val getNewEventNotificationFlowUseCase: GetNewEventNotificationFlowUseCase,
    private val massSendLimiter: MassSendLimiter,
    private val timeZone: TimeZone
) {
    context(BehaviourContext)
    fun setup() = launch {
        runCatching {
            getNewEventNotificationFlowUseCase().collect { notification ->
                massSendLimiter.wait()
                runCatching {
                    sendEventMessage(notification.userId.toChatId(), notification.event, timeZone)
                }.onFailure { exception ->
                    logger.error("Couldn't send new event notification to ${notification.userId}", exception)
                }
            }
        }.onFailure { exception ->
            logger.error("Exception during collection", exception)
        }
    }
}
