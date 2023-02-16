package mute.data.usecases

import common.domain.Transaction
import mute.data.repository.MuteSettingsRepository
import org.koin.core.annotation.Single

@Single
class ContainsByIdMuteSettingsUseCase(
    private val muteSettingsRepository: MuteSettingsRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long): Boolean = transaction {
        return@transaction muteSettingsRepository.containsById(userId)
    }
}