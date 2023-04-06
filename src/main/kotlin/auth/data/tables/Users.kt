package auth.data.tables

import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.select
import organizations.data.tables.Organizations
import organizations.data.tables.toDomainModel

object Users : LongIdTable() {
    val phoneNumber: Column<String> = varchar("phone_number", length = 15).uniqueIndex()
    val course: Column<Course> = enumeration<Course>("course").index()
    val name: Column<String> = varchar("name", length = 256)
    val job: Column<String> = varchar("job", length = 256)
    val organizationType: Column<OrganizationType> = enumeration<OrganizationType>("organization_type").index()
    val organizationId: Column<EntityID<Long>> = reference("organization_id", Organizations)
    val activityDescription: Column<String> = varchar("activity_description", length = 1024)
    val isApproved: Column<Boolean> = bool("is_approved").default(false)

    class Entity(id: EntityID<Long>) : LongEntity(id) {
        companion object : LongEntityClass<Entity>(Users)

        var phoneNumber by Users.phoneNumber
        var course by Users.course
        var name by Users.name
        var job by Users.job
        var organizationType by Users.organizationType
        var organization by Organizations.Entity referencedOn Users.organizationId
        var activityDescription by Users.activityDescription
        var isApproved by Users.isApproved
    }
}

fun Users.Entity.toDomainModel() = User.Details(
    id = id.value,
    phoneNumber = requireNotNull(PhoneNumber.of(phoneNumber)),
    course = course,
    name = name,
    job = job,
    organizationType = organizationType,
    organization = organization.toDomainModel(),
    activityDescription = activityDescription,
    isApproved = isApproved,
    areas = UserAreas.select { UserAreas.userId eq id }.map { it[UserAreas.area] }.toSet()
)
