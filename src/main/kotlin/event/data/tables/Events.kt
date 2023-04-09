package event.data.tables

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Events : Table() {
    val name: Column<String> = varchar("name", length = 256)
    val timestampBegin: Column<Instant> = timestamp("timestamp_begin").index()
    val timestampEnd: Column<Instant> = timestamp("timestamp_end").index()
    val description: Column<String> = varchar("description", length = 1024).default("")
    val url: Column<String> = varchar("url", length = 1024)
}
