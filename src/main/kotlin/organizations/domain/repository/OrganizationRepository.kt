package organizations.domain.repository

import organizations.domain.entities.Organization

interface OrganizationRepository {
    fun add(organization: Organization.New): Organization
    fun get(id: Long): Organization?
    fun getByName(name: String): Organization?
    fun getByCityId(cityId: Long): List<Organization>
}
