package locations.data.repository

import locations.data.tables.LocationTable
import locations.domain.entities.Location
import locations.domain.repository.LocationRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single

@Single
class LocationRepositoryImpl : LocationRepository {
    override fun add(location: Location) {
        val table = LocationTable.from(location)
        table.insert { table.insertBody(it, location) }
    }

    override fun getChildren(location: Location.WithChildren): List<Location> {
        val childrenTable = LocationTable.from(location).childrenTable
        return childrenTable
            .select { childrenTable.parentId eq location.id }
            .map(childrenTable::map)
    }
}