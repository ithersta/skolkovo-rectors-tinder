package notifications.domain.repository

import notifications.domain.entities.NotificationPreference

interface NotificationPreferenceRepository {
    fun getUserIdsWithPreference(notificationPreference: NotificationPreference): List<Long>
    fun get(userId: Long): NotificationPreference?
    fun update(userId: Long, notificationPreference: NotificationPreference)
}
