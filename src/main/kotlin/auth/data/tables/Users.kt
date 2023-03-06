package auth.data.tables

import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object Users : LongIdTable() {
    val phoneNumber: Column<String> = reference("phone_number", PhoneNumbers.phoneNumber).uniqueIndex()
    val name: Column<String> = varchar("name", length = 256)
    val course: Column<Course> = enumeration<Course>("course").index()
    val city: Column<String> = varchar("city", length = 256)
    val job: Column<String> = varchar("job", length = 256)
    val organizationType: Column<OrganizationType> = enumeration<OrganizationType>("organizationType").index()
    val organization: Column<String> = varchar("organization", length = 256)
    val activityDescription: Column<String> = varchar("activity_description", length = 1024)
}
