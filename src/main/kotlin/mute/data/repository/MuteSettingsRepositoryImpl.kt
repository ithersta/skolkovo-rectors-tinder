package mute.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import mute.data.entities.MuteSettings
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single
import kotlin.time.Duration

@Single
class MuteSettingsRepositoryImpl : MuteSettingsRepository {
    override fun insert(userIdVal: Long, duration: Duration) {
        MuteSettings.insert {
            it[userId] = userIdVal
            it[until] = Clock.System.now().plus(duration)
        }
    }

    override fun delete(userIdVal: Long) {
        MuteSettings.deleteWhere(op = { MuteSettings.userId eq userIdVal })
    }

    override fun getEarliest(): Pair<Long, Instant>? {
        val res = MuteSettings.selectAll().minByOrNull { row ->
            row[MuteSettings.until]
        }
        return if (res != null) {
            Pair(res[MuteSettings.userId].value, res[MuteSettings.until])
        } else {
            null
        }
    }
}
