package event.telegram.flows

import auth.domain.repository.UserRepository
import common.domain.Transaction
import config.BotConfig
import dev.inmo.tgbotapi.extensions.utils.flatMap
import event.domain.entities.Event
import event.domain.usecases.AddEventUseCase
import org.koin.core.annotation.Single

@Single
class GetNewEventNotificationFlowUseCase(
    private val addEventUseCase: AddEventUseCase,
    private val userRepository: UserRepository,
    private val botConfig: BotConfig,
    private val transaction: Transaction
) {
    class Notification(
        val userId: Long,
        val event: Event
    )
    operator fun invoke() = addEventUseCase.newEventsFlow
        .flatMap { event ->
            transaction {
                userRepository.getAllActiveExceptUser(botConfig.adminId)
            }.map { Notification(it.id, event) }
        }
}
