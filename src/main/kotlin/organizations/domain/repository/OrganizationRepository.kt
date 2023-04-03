package organizations.domain.repository

import organizations.domain.entities.Organization

interface OrganizationRepository {
    fun add(organization: Organization.New)
    fun getByCityId(cityId: Long): List<Organization>
    fun get(id: Long): Organization?
}
