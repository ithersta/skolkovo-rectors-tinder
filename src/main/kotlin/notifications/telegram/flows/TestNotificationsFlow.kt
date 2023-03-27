package notifications.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import dev.inmo.tgbotapi.extensions.api.send.sendActionTyping
import generated.RoleFilterBuilder
import notifications.domain.entities.NotificationPreference
import notifications.domain.usecases.GetDelayedQuestionsNotificationFlowUseCase
import org.koin.core.component.inject

fun RoleFilterBuilder<User.Admin>.testNotificationsFlow() {
    val getDelayedQuestionsNotificationFlowUseCase: GetDelayedQuestionsNotificationFlowUseCase by inject()
    anyState {
        listOf(
            "test_daily_notifications" to NotificationPreference.Daily,
            "test_weekly_notifications" to NotificationPreference.Weekly
        ).forEach { (command, notificationPreference) ->
            onCommand(command, description = null) { message ->
                getDelayedQuestionsNotificationFlowUseCase.triggerTestNotification(notificationPreference)
                sendActionTyping(message.chat)
            }
        }
    }
}
