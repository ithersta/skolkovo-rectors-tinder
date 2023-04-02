package locations.domain.repository

import locations.domain.entities.Location

interface LocationRepository {
    fun add(location: Location)
    fun getChildren(location: Location.WithChildren): List<Location.WithParent>
    fun getAllCountries(): List<Location.Country>
}
