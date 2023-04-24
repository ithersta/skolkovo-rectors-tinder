package common.domain

import arrow.core.raise.either
import arrow.core.raise.ensure

data class MaxLengthExceeded(val maxLength: Int)

fun String.ensureMaxLength(maxLength: Int) = either {
    ensure(length <= maxLength) { MaxLengthExceeded(maxLength) }
    this@ensureMaxLength
}
