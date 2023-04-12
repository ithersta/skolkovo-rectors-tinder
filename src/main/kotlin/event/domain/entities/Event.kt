package event.domain.entities

import kotlinx.datetime.Instant

data class Event(
    val name: String,
    val timestampBegin: Instant,
    val timestampEnd: Instant,
    val description: String? = null,
    val url: String
)
