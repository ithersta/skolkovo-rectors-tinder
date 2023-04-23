package event.domain.usecases

import common.domain.Transaction
import event.domain.entities.Event
import event.domain.repository.EventRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single

@Single
class AddEventUseCase(
    private val eventRepository: EventRepository,
    private val transaction: Transaction
) {
    private val _newEventsFlow = MutableSharedFlow<Event>()
    val newEventsFlow = _newEventsFlow.asSharedFlow()
    suspend operator fun invoke(event: Event) = transaction {
        val id = eventRepository.add(event)
        event.copy(id = id)
    }.also {
        _newEventsFlow.emit(it)
    }
}
