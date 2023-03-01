package notifications.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import notifications.domain.entities.NotificationPreference

@Serializable
@SerialName("cnp")
data class ChangeNotificationPreferenceQuery(
    val newPreference: NotificationPreference
) : Query
