package mute.domain.usecases

import common.domain.Transaction
import mute.domain.repository.MuteSettingsRepository
import org.koin.core.annotation.Single

@Single
class DeleteMuteSettingsUseCase(
    private val muteSettingsRepository: MuteSettingsRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long) = transaction {
        muteSettingsRepository.delete(userId)
    }
}
