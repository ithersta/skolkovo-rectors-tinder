package organizations.data.repository

import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import organizations.data.tables.OrganizationCities
import organizations.data.tables.Organizations
import organizations.data.tables.toDomainModel
import organizations.domain.entities.Organization
import organizations.domain.repository.OrganizationRepository

@Single
class OrganizationRepositoryImpl : OrganizationRepository {
    override fun add(organization: Organization.New): Organization {
        return Organizations.Entity.new {
            name = organization.name
        }.toDomainModel()
    }

    override fun addCity(id: Long, cityId: Long) {
        OrganizationCities.insertIgnore {
            it[organizationId] = id
            it[OrganizationCities.cityId] = cityId
        }
    }

    override fun getByCityId(cityId: Long): List<Organization> {
        return Organizations
            .innerJoin(OrganizationCities)
            .slice(Organizations.columns)
            .select { OrganizationCities.cityId eq cityId }
            .let { Organizations.Entity.wrapRows(it) }
            .map(Organizations.Entity::toDomainModel)
    }

    override fun get(id: Long): Organization? {
        return Organizations.Entity.findById(id)?.toDomainModel()
    }

    override fun getByName(name: String): Organization? {
        return Organizations.Entity.find { Organizations.name eq name }.singleOrNull()?.toDomainModel()
    }
}
