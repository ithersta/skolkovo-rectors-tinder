package organizations.domain.repository

import organizations.domain.entities.City

interface CityRepository {
    fun add(city: City.New): City
    fun get(id: Long): City?
    fun getByName(name: String): City?
    fun getAll(): List<City>
}
