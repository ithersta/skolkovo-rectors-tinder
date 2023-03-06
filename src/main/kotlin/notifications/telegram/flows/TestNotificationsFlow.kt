package notifications.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import dev.inmo.tgbotapi.extensions.api.send.sendActionTyping
import generated.RoleFilterBuilder
import notifications.domain.entities.NotificationPreference
import notifications.domain.usecases.GetNewQuestionsNotificationFlowUseCase
import org.koin.core.component.inject

fun RoleFilterBuilder<User.Admin>.testNotificationsFlow() {
    val getNewQuestionsNotificationFlowUseCase: GetNewQuestionsNotificationFlowUseCase by inject()
    anyState {
        onCommand("notify questions daily", description = null) { message ->
            getNewQuestionsNotificationFlowUseCase.triggerTestNotification(NotificationPreference.Daily)
            sendActionTyping(message.chat)
        }
        onCommand("notify questions weekly", description = null) { message ->
            getNewQuestionsNotificationFlowUseCase.triggerTestNotification(NotificationPreference.Weekly)
            sendActionTyping(message.chat)
        }
    }
}
