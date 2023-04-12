package organizations.data.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import organizations.domain.entities.Organization

object Organizations : LongIdTable() {
    val name = varchar("name", length = 256).uniqueIndex()

    class Entity(id: EntityID<Long>) : LongEntity(id) {
        companion object : LongEntityClass<Entity>(Organizations)

        var name by Organizations.name
    }
}

fun Organizations.Entity.toDomainModel() = Organization(
    id = id.value,
    name = name
)
