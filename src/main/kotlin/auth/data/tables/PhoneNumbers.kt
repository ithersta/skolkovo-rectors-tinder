package auth.data.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PhoneNumbers : Table() {
    val phoneNumber: Column<String> = char(name = "phone_number", length = 11)
    val isActive: Column<Boolean> = bool("is_active")
    override val primaryKey = PrimaryKey(phoneNumber)
}
