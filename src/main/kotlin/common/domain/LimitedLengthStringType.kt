package common.domain

import arrow.core.raise.either
import arrow.core.raise.ensure

data class MaxLengthExceeded(val maxLength: Int)

open class LimitedLengthStringType<T : Any>(
    val maxLength: Int,
    private val construct: (String) -> T
) {
    fun of(value: String) = either {
        ensure(value.length <= maxLength) { MaxLengthExceeded(maxLength) }
        construct(value)
    }

    fun ofTruncated(value: String) = construct(value.take(maxLength))
}
