package organizations.domain.entities

class Organization(
    val id: Long,
    val name: String
) {
    data class New(
        val name: String
    )
}
