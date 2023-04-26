package event.data.tables

import event.domain.entities.Event
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Events : LongIdTable() {
    val name: Column<String> = varchar("name", length = Event.Name.maxLength)
    val timestampBegin: Column<Instant> = timestamp("timestamp_begin").index()
    val timestampEnd: Column<Instant> = timestamp("timestamp_end").index()
    val description: Column<String?> = varchar("description", length = Event.Description.maxLength).nullable()
    val url: Column<String> = varchar("url", length = Event.Url.maxLength)
}
