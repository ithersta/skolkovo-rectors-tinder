package event.domain.usecases

import common.domain.Paginated
import common.domain.Transaction
import event.domain.entities.Event
import event.domain.repository.EventRepository
import org.koin.core.annotation.Single

@Single
class GetEventsPaginatedUseCase(
    private val eventRepository: EventRepository,
    private val transaction: Transaction
) {
    operator fun invoke(offset: Int, limit: Int): Paginated<Event> = transaction {
        return@transaction eventRepository.getAllPaginated(offset, limit)
    }
}
