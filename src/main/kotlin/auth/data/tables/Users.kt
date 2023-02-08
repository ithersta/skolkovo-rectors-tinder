package auth.data.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object Users : LongIdTable() {
    val phoneNumber: Column<String> = reference("phone_number", PhoneNumbers.phoneNumber).uniqueIndex()
    val name: Column<String> = varchar("name", length = 256)
    val city: Column<String> = varchar("city", length = 256)
    val job: Column<String> = varchar("job", length = 256)
    val organization: Column<String> = varchar("organization", length = 256)
    val professionalAreas: Column<String> = varchar("professional_areas", length = 1024)
    val activityDescription: Column<String> = varchar("activity_description", length = 1024)
}
