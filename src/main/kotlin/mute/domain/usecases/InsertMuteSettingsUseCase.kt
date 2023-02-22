package mute.domain.usecases

import common.domain.Transaction
import kotlinx.datetime.Clock
import mute.domain.repository.MuteSettingsRepository
import org.koin.core.annotation.Single
import kotlin.time.Duration

@Single
class InsertMuteSettingsUseCase(
    private val muteSettingsRepository: MuteSettingsRepository,
    private val clock: Clock,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long, duration: Duration) = transaction {
        muteSettingsRepository.insert(userId, clock.now().plus(duration))
    }
}
