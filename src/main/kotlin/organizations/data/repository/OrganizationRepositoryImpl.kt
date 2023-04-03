package organizations.data.repository

import org.jetbrains.exposed.dao.id.EntityID
import org.koin.core.annotation.Single
import organizations.data.tables.Cities
import organizations.data.tables.Organizations
import organizations.data.tables.toDomainModel
import organizations.domain.entities.Organization
import organizations.domain.repository.OrganizationRepository

@Single
class OrganizationRepositoryImpl : OrganizationRepository {
    override fun add(organization: Organization.New) {
        Organizations.Entity.new {
            name = organization.name
            cityId = EntityID(organization.cityId, Cities)
        }
    }

    override fun getByCityId(cityId: Long): List<Organization> {
        return Organizations.Entity
            .find { Organizations.cityId eq cityId }
            .map(Organizations.Entity::toDomainModel)
    }

    override fun get(id: Long): Organization? {
        return Organizations.Entity.findById(id)?.toDomainModel()
    }
}
