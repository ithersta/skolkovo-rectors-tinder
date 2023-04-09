package event.domain.entities

import java.sql.Timestamp

data class Event(
    val name: String,
    val timestampBegin: Timestamp,
    val timestampEnd: Timestamp,
    val description: String? = null,
    val url: String
)
