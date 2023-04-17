package event.domain.usecases

import common.domain.Transaction
import event.domain.entities.Event
import event.domain.repository.EventRepository
import org.koin.core.annotation.Single

@Single
class GetEventByIdUseCase(
    private val eventRepository: EventRepository,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long): Event = transaction {
        return@transaction eventRepository.getById(id)
    }
}
