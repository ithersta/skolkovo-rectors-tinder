package organizations.data.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import organizations.domain.entities.City

object Cities : LongIdTable() {
    val name = varchar("name", length = City.Name.maxLength).uniqueIndex()

    class Entity(id: EntityID<Long>) : LongEntity(id) {
        companion object : LongEntityClass<Entity>(Cities)

        var name by Cities.name
    }
}

fun Cities.Entity.toDomainModel() = City(
    id = id.value,
    name = City.Name.ofTruncated(name)
)
