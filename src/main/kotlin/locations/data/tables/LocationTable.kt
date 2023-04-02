package locations.data.tables

import locations.domain.entities.Location
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

sealed class LocationTable<L : Location> : LongIdTable() {
    open val name = varchar("name", length = 256)
    abstract fun map(row: ResultRow): L
    abstract fun insertBody(it: InsertStatement<Number>, location: L)

    sealed class WithParent<L : Location>(val parentTable: LocationTable<*>) : LocationTable<L>() {
        val parentId = reference("parent_id", parentTable)
    }

    interface WithChildren {
        val childrenTable: WithParent<*>
    }

    object Countries : LocationTable<Location.Country>(), WithChildren {
        override val name = super.name.uniqueIndex()
        override val childrenTable = Districts
        override fun map(row: ResultRow) = Location.Country(row[id].value, row[name])
        override fun insertBody(it: InsertStatement<Number>, location: Location.Country) {
            it[name] = location.name
        }
    }

    object Districts : WithParent<Location.District>(parentTable = Countries), WithChildren {
        override val childrenTable = Regions
        override fun map(row: ResultRow) = Location.District(row[id].value, row[name], row[parentId].value)
        override fun insertBody(it: InsertStatement<Number>, location: Location.District) {
            it[name] = location.name
            it[parentId] = location.parentId
        }
    }

    object Regions : WithParent<Location.Region>(parentTable = Districts), WithChildren {
        override val childrenTable = Cities
        override fun map(row: ResultRow) = Location.Region(row[id].value, row[name], row[parentId].value)
        override fun insertBody(it: InsertStatement<Number>, location: Location.Region) {
            it[name] = location.name
            it[parentId] = location.parentId
        }
    }

    object Cities : WithParent<Location.City>(parentTable = Regions) {
        override fun map(row: ResultRow) = Location.City(row[id].value, row[name], row[parentId].value)
        override fun insertBody(it: InsertStatement<Number>, location: Location.City) {
            it[name] = location.name
            it[parentId] = location.parentId
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <L : Location> from(location: L): LocationTable<L> = when (location) {
            is Location.Country -> Countries as LocationTable<L>
            is Location.District -> Districts as LocationTable<L>
            is Location.Region -> Regions as LocationTable<L>
            is Location.City -> Cities as LocationTable<L>
            else -> error("Impossible")
        }

        fun from(location: Location.WithChildren): WithChildren = when (location) {
            is Location.Country -> Countries
            is Location.District -> Districts
            is Location.Region -> Regions
        }
    }
}
