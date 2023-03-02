package notifications.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.launch
import notifications.domain.usecases.GetNewQuestionsNotificationFlowUseCase
import notifications.telegram.flows.newQuestionsPager
import org.koin.core.annotation.Single

@Single
class NewQuestionsNotificationSender(
    private val getNewQuestionsNotificationFlow: GetNewQuestionsNotificationFlowUseCase
) {
    fun BehaviourContext.setup() = launch {
        getNewQuestionsNotificationFlow().collect { notification ->
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
