package organizations.domain.entities

import common.domain.LimitedStringCompanion
import kotlinx.serialization.Serializable

class Organization(
    val id: Long,
    val name: Name
) {
    data class New(
        val name: String
    )

    @Serializable
    @JvmInline
    value class Name private constructor(val value: String) {
        companion object : LimitedStringCompanion<Name>(maxLength = 256, { Name(it) })
    }
}
