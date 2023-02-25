package notifications.domain.repository

import notifications.domain.entities.NotificationPreference

interface NotificationPreferenceRepository {
    fun getUserIdsWithPreference(notificationPreference: NotificationPreference): List<Long>
}
