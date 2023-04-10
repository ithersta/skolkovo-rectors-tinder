package event.domain.repository

import event.domain.entities.Event

interface EventRepository {
    fun add(event: Event)
    fun getAll():List<Event>
}
