package mute.domain.entities

import kotlinx.datetime.Instant

data class MuteSettingsRow(
    val userId: Long,
    val until: Instant
)