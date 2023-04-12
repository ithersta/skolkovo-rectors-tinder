package event.domain.repository

import event.domain.entities.Event

interface EventRepository {
    fun add(event: Event)
    fun getAll(): List<Event>
    fun getById(id: Int): Event
    fun removeById(id: Int)

}
