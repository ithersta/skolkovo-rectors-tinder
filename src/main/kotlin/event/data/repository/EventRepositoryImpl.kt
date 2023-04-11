package event.data.repository

import event.data.tables.Events
import event.domain.entities.Event
import event.domain.repository.EventRepository
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single

@Single
class EventRepositoryImpl : EventRepository {
    override fun add(event: Event) {
        Events.insert {
            it[name] = event.name
            // it[timestampBegin] = event.timestampBegin
            // it[timestampEnd] = event.timestampEnd
            it[description] = event.description
            it[url] = event.url
        }
    }

    override fun getAll(): List<Event> {
        TODO("Not yet implemented")
    }
}
