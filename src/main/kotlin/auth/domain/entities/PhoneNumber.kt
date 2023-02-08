package auth.domain.entities

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
        ).takeIf { it.value.length == 11 && it.value.startsWith('7') }
    }
}
