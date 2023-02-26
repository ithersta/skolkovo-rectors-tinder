package notifications.domain.usecases

import common.domain.Transaction
import notifications.domain.entities.NotificationPreference
import notifications.domain.repository.NotificationPreferenceRepository
import org.koin.core.annotation.Single

@Single
class UpdateNotificationPreferenceUseCase(
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long, notificationPreference: NotificationPreference) = transaction {
        notificationPreferenceRepository.update(userId, notificationPreference)
    }
}
