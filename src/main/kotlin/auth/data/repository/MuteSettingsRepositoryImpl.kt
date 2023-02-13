package auth.data.repository

import auth.domain.repository.MuteSettingsRepository
import mute.data.entities.MuteSettings
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single

@Single
class MuteSettingsRepositoryImpl: MuteSettingsRepository {
    override fun getAll(): List<Long> {
        return MuteSettings.selectAll().map { it[MuteSettings.userId].value }
    }

}