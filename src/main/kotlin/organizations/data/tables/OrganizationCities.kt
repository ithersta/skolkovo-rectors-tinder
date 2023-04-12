package organizations.data.tables

import org.jetbrains.exposed.sql.Table

object OrganizationCities : Table() {
    val organizationId = reference("organization_id", Organizations)
    val cityId = reference("city_id", Cities)
    override val primaryKey = PrimaryKey(organizationId, cityId)
}
