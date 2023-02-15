package auth.domain.repository

interface MuteSettingsRepository {
    fun getAll(): List<Long>
}
