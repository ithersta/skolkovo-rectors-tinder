package organizations.domain.repository

import organizations.domain.entities.Organization

interface OrganizationRepository {
    fun getAll(): List<Organization>
    fun add(organization: Organization.New): Organization
    fun addCity(id: Long, cityId: Long)
    fun get(id: Long): Organization?
    fun getByName(name: String): Organization?
    fun getByCityId(cityId: Long): List<Organization>
}
