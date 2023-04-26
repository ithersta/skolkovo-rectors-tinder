package organizations.domain.entities

import common.domain.LimitedLengthStringType
import kotlinx.serialization.Serializable

data class City(
    val id: Long,
    val name: Name
) {
    data class New(
        val name: Name
    )

    @Serializable
    @JvmInline
    value class Name private constructor(val value: String) {
        companion object : LimitedLengthStringType<Name>(maxLength = 256, { Name(it) })
    }
}
