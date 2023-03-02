package notifications.data.repository

import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import notifications.domain.entities.NotificationPreference
import notifications.domain.repository.NotificationPreferenceRepository
import org.jetbrains.exposed.sql.except
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single

@Single
class NotificationPreferenceRepositoryImpl : NotificationPreferenceRepository {
    override fun getUserIdsWithPreference(notificationPreference: NotificationPreference): List<Long> {
        return NotificationPreferences
            .slice(NotificationPreferences.userId)
            .select { NotificationPreferences.preference eq notificationPreference }
            .except(MuteSettings.slice(MuteSettings.userId).selectAll())
            .map { it[NotificationPreferences.userId].value }
    }

    override fun get(userId: Long): NotificationPreference? {
        return NotificationPreferences
            .slice(NotificationPreferences.preference)
            .select { NotificationPreferences.userId eq userId }
            .firstOrNull()
            ?.let { it[NotificationPreferences.preference] }
    }

    override fun update(userId: Long, notificationPreference: NotificationPreference) {
        NotificationPreferences.replace {
            it[NotificationPreferences.userId] = userId
            it[NotificationPreferences.preference] = notificationPreference
        }
    }
}
