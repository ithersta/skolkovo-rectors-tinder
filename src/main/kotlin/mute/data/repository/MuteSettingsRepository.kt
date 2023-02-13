package mute.data.repository

import kotlinx.datetime.Instant
import kotlin.time.Duration

interface MuteSettingsRepository {
    fun insert(userIdVal: Long, dateMute: Instant)
    fun delete(userIdVal: Long)
    fun getEarliest(): Pair<Long, Instant>?
}
