package notifications.data.tables

import auth.data.tables.Users
import notifications.domain.entities.NotificationPreference
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object NotificationPreferences : Table() {
    val userId: Column<EntityID<Long>> = reference("user_id", Users)
    val preference: Column<NotificationPreference> = enumeration("preference")
    override val primaryKey = PrimaryKey(userId)
}
