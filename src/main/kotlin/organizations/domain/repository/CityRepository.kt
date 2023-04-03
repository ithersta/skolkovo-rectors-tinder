package organizations.domain.repository

import organizations.domain.entities.City

interface CityRepository {
    fun add(city: City.New)
    fun getAll(): List<City>
}
