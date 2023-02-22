package mute.domain.usecases

import common.domain.Transaction
import mute.domain.repository.MuteSettingsRepository
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
