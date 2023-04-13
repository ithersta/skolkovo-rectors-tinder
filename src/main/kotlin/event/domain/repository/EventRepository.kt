package event.domain.repository

import common.domain.Paginated
import event.domain.entities.Event

interface EventRepository {
    fun add(event: Event)
    fun getAll(): List<Event>
    fun delete(idDel: Long)
    fun getAllPaginated(offset: Int, limit: Int): Paginated<Event>
}
