package mute.data.repository

import kotlinx.datetime.Instant
import mute.data.entities.MuteSettings
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single

@Single
class MuteSettingsRepositoryImpl : MuteSettingsRepository {
    override fun insert(userIdVal: Long, dateMute: Instant) {
        MuteSettings.insert {
            it[userId] = userIdVal
            it[until] = dateMute
        }
    }

    override fun delete(userIdVal: Long) {
        MuteSettings.deleteWhere(op = { MuteSettings.userId eq userIdVal })
    }

    override fun getEarliest(): Pair<Long, Instant>? {
        val res = MuteSettings.selectAll().orderBy(MuteSettings.until).limit(1).firstOrNull()
        return res?.let { Pair(it[MuteSettings.userId].value, it[MuteSettings.until]) }
    }

    override fun containsById(userIdVal: Long): Boolean {
        return MuteSettings.select { MuteSettings.userId eq userIdVal }.count() > 0
    }
}
