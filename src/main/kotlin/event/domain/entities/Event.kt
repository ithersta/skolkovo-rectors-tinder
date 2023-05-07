package event.domain.entities

import arrow.core.raise.either
import arrow.core.raise.ensure
import common.domain.LimitedLengthStringType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.UrlValidator

data class Event(
    val name: Name,
    val timestampBegin: Instant,
    val timestampEnd: Instant,
    val description: Description? = null,
    val url: Url,
    val id: Long? = null
) {
    @Serializable
    @JvmInline
    value class Name private constructor(val value: String) {
        companion object : LimitedLengthStringType<Name>(maxLength = 256, { Name(it) })
    }

    @Serializable
    @JvmInline
    value class Description private constructor(val value: String) {
        companion object : LimitedLengthStringType<Description>(maxLength = 1024, { Description(it) })
    }

    @Serializable
    @JvmInline
    value class Url private constructor(val value: String) {
        sealed interface Error {
            class MaxLengthExceeded(val maxLength: Int) : Error
            object InvalidUrl : Error
        }

        companion object {
            const val maxLength = 1024
            private val urlValidator =
                UrlValidator()

            fun of(value: String) = either {
                ensure(value.length <= maxLength) { Error.MaxLengthExceeded(maxLength) }
                ensure(urlValidator.isValid(value)) { Error.InvalidUrl }
                Url(value)
            }
        }
    }
}
