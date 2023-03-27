package notifications.domain.entities

import kotlinx.datetime.Instant

class DelayedNewQuestionsNotification(
    val userId: Long,
    val from: Instant,
    val until: Instant,
    val notificationPreference: NotificationPreference
)
