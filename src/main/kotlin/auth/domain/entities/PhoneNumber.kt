package auth.domain.entities

@JvmInline
value class PhoneNumber private constructor(
    val value: String
) {
    companion object {
        fun of(value: String) = PhoneNumber(value.filter { it.isDigit() })
            .takeIf { it.value.length == 11 && it.value.startsWith('7') }
    }
}
