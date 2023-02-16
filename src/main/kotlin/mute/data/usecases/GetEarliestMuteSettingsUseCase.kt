package mute.data.usecases

import common.domain.Transaction
import kotlinx.datetime.Instant
import mute.data.entities.MuteSettingsRow
import mute.data.repository.MuteSettingsRepository
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
