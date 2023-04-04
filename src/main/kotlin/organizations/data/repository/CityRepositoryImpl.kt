package organizations.data.repository

import org.koin.core.annotation.Single
import organizations.data.tables.Cities
import organizations.data.tables.toDomainModel
import organizations.domain.entities.City
import organizations.domain.repository.CityRepository

@Single
class CityRepositoryImpl : CityRepository {
    override fun add(city: City.New) {
        Cities.Entity.new {
            name = city.name
        }
    }

    override fun getAll(): List<City> {
        return Cities.Entity.all().map(Cities.Entity::toDomainModel)
    }

    override fun get(id: Long): City? {
        return Cities.Entity.findById(id)?.toDomainModel()
    }

    override fun getByName(name: String): City? {
        return Cities.Entity.find { Cities.name eq name }.singleOrNull()?.toDomainModel()
    }
}
