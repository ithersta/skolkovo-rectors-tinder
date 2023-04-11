package event.domain.entities

import java.time.OffsetDateTime

data class Event(
    val name: String,
    val timestampBegin: OffsetDateTime,
    val timestampEnd: OffsetDateTime,
    val description: String = "",
    val url: String
)
