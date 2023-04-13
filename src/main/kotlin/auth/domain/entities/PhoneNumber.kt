package auth.domain.entities

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PhoneNumber private constructor(
    val value: String
) {
    companion object {
        private val punctuation = setOf('-', '(', ')')

        fun of(value: String) = PhoneNumber(
            value = value
                .filterNot { it.isWhitespace() }
                .filterNot { it in punctuation }
                .removePrefix("+")
        ).takeIf { "\\d{11}".toRegex().matches(it.value) }
    }

    override fun toString(): String {
        return "+$value"
    }
}
