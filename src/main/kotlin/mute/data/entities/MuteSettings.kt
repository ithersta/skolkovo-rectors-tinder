package mute.data.entities

import auth.data.tables.Users
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object MuteSettings : LongIdTable() {
    val userId: Column<EntityID<Long>> = reference("user_id", Users)
    val from: Column<Instant> = timestamp("from_timestamp")
    val until: Column<Instant> = timestamp("until_timestamp")
}
