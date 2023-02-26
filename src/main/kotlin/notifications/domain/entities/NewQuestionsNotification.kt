package notifications.domain.entities

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class NewQuestionsNotification(val userId: Long, val from: Instant, val until: Instant)
