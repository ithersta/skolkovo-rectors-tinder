package notifications.domain.entities

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

enum class NotificationPreference(val duration: Duration) {
    RightAway(0.seconds),
    Daily(1.days),
    Weekly(7.days)
}
