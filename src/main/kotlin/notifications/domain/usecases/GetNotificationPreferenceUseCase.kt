package notifications.domain.usecases

import common.domain.Transaction
import notifications.domain.entities.NotificationPreference
import notifications.domain.repository.NotificationPreferenceRepository
import org.koin.core.annotation.Single

@Single
class GetNotificationPreferenceUseCase(
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long) = transaction {
        notificationPreferenceRepository.get(userId) ?: NotificationPreference.RightAway
    }
}
