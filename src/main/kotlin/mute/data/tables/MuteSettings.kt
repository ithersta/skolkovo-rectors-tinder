package mute.data.tables

import auth.data.tables.Users
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object MuteSettings : Table() {
    val userId: Column<EntityID<Long>> = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val until: Column<Instant> = timestamp("until").index()
    override val primaryKey = PrimaryKey(userId)
}
