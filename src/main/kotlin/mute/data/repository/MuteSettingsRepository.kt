package mute.data.repository

import kotlinx.datetime.Instant
import kotlin.time.Duration

interface MuteSettingsRepository {
    fun insert(userIdVal: Long, duration: Duration)
    fun delete(userIdVal: Long)
    fun getEarliest(): Pair<Long, Instant>?
}
