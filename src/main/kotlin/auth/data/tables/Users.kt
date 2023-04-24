package auth.data.tables

import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.select
import organizations.data.tables.Cities
import organizations.data.tables.Organizations
import organizations.data.tables.toDomainModel

object Users : LongIdTable() {
    val phoneNumber = varchar("phone_number", length = 15).uniqueIndex()
    val course = enumeration<Course>("course").index()
    val name = varchar("name", length = User.Name.maxLength)
    val job = varchar("job", length = User.Job.maxLength)
    val cityId = reference("city", Cities)
    val organizationType = enumeration<OrganizationType>("organization_type").index()
    val organizationId = reference("organization_id", Organizations)
    val activityDescription = varchar("activity_description", length = User.ActivityDescription.maxLength)
    val isApproved = bool("is_approved").default(false)

    class Entity(id: EntityID<Long>) : LongEntity(id) {
        companion object : LongEntityClass<Entity>(Users)

        var phoneNumber by Users.phoneNumber
        var course by Users.course
        var name by Users.name
        var job by Users.job
        var city by Cities.Entity referencedOn cityId
        var organizationType by Users.organizationType
        var organization by Organizations.Entity referencedOn organizationId
        var activityDescription by Users.activityDescription
        var isApproved by Users.isApproved
    }
}

fun Users.Entity.toDomainModel() = User.Details(
    id = id.value,
    phoneNumber = checkNotNull(PhoneNumber.of(phoneNumber)),
    course = course,
    name = checkNotNull(User.Name.of(name).getOrNull()),
    job = checkNotNull(User.Job.of(job).getOrNull()),
    city = city.toDomainModel(),
    organizationType = organizationType,
    organization = organization.toDomainModel(),
    activityDescription = checkNotNull(User.ActivityDescription.of(activityDescription).getOrNull()),
    isApproved = isApproved,
    areas = UserAreas.select { UserAreas.userId eq id }.map { it[UserAreas.area] }.toSet()
)
