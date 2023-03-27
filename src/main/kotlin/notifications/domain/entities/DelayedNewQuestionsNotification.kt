package notifications.domain.entities

import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantComponentSerializer
import kotlinx.serialization.Serializable

class DelayedNewQuestionsNotification(
    val userId: Long,
    val from: Instant,
    val until: Instant,
    val notificationPreference: NotificationPreference
)
