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
    override fun add(event: Event) {
        Events.insert {
            it[name] = event.name
            it[timestampBegin] = event.timestampBegin
            it[timestampEnd] = event.timestampEnd
            it[description] = event.description
            it[url] = event.url
        }
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
                name = row[Events.name],
                timestampBegin = row[Events.timestampBegin],
                timestampEnd = row[Events.timestampEnd],
                description = row[Events.description],
                url = row[Events.url],
                id = row[Events.id].value
            )
        }
    }
}
