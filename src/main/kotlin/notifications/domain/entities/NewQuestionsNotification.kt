package notifications.domain.entities

import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantComponentSerializer
import kotlinx.serialization.Serializable

@Serializable
class NewQuestionsNotification(
    val userId: Long,
    @Serializable(with = InstantComponentSerializer::class)
    val from: Instant,
    @Serializable(with = InstantComponentSerializer::class)
    val until: Instant,
    val notificationPreference: NotificationPreference
)
