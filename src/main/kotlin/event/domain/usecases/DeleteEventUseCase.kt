package event.domain.usecases

import common.domain.Transaction
import event.domain.repository.EventRepository
import org.koin.core.annotation.Single

@Single
class DeleteEventUseCase(
    private val eventRepository: EventRepository,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long) = transaction {
        eventRepository.delete(id)
    }
}
