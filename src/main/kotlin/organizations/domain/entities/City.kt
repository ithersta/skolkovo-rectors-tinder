package organizations.domain.entities

data class City(
    val id: Long,
    val name: String
) {
    data class New(
        val name: String
    )
}
