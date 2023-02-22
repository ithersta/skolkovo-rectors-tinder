package mute.domain.usecases

import common.domain.Transaction
import mute.domain.entities.MuteSettingsRow
import mute.domain.repository.MuteSettingsRepository
import org.koin.core.annotation.Single

@Single
class GetEarliestMuteSettingsUseCase(
    private val muteSettingsRepository: MuteSettingsRepository,
    private val transaction: Transaction
) {
    operator fun invoke(): MuteSettingsRow? = transaction {
        return@transaction muteSettingsRepository.getEarliest()
    }
}
