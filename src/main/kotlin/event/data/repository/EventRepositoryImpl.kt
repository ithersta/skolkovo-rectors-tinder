package event.data.repository

import common.domain.Paginated
import event.data.tables.Events
import event.domain.entities.Event
import event.domain.repository.EventRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single

@Single
class EventRepositoryImpl : EventRepository {
    override fun add(event: Event): Long {
        val id = Events.insertAndGetId {
            it[name] = event.name.value
            it[timestampBegin] = event.timestampBegin
            it[timestampEnd] = event.timestampEnd
            it[description] = event.description?.value
            it[url] = event.url.value
        }
        return id.value
    }

    override fun getById(id: Long): Event {
        return Events.select { Events.id eq id }.map(::mapper).first()
    }

    override fun getAll(): List<Event> {
        return Events.selectAll().map(::mapper)
    }

    override fun delete(idDel: Long) {
        Events.deleteWhere { id eq idDel }
    }

    override fun getAllPaginated(offset: Int, limit: Int): Paginated<Event> {
        val list = {
            Events.selectAll()
        }
        return Paginated(
            slice = list().limit(limit, offset.toLong()).map(::mapper),
            count = list().count().toInt()
        )
    }

    companion object {
        fun mapper(row: ResultRow): Event {
            return Event(
                name = Event.Name.ofTruncated(row[Events.name]),
                timestampBegin = row[Events.timestampBegin],
                timestampEnd = row[Events.timestampEnd],
                description = row[Events.description]?.let { Event.Description.ofTruncated(it) },
                url = checkNotNull(Event.Url.of(row[Events.url]).getOrNull()),
                id = row[Events.id].value
            )
        }
    }
}
