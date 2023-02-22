package mute.domain.repository

import kotlinx.datetime.Instant
import mute.domain.entities.MuteSettingsRow

interface MuteSettingsRepository {
    fun insert(userIdVal: Long, dateMute: Instant)
    fun delete(userIdVal: Long)
    fun getEarliest(): MuteSettingsRow?
    fun containsById(userIdVal: Long): Boolean
}
