package mute.data.repository

import kotlinx.datetime.Instant

interface MuteSettingsRepository {
    fun insert(userIdVal: Long, dateMute: Instant)
    fun delete(userIdVal: Long)
    fun getEarliest(): Pair<Long, Instant>?
    fun containsById(userIdVal: Long): Boolean
}
