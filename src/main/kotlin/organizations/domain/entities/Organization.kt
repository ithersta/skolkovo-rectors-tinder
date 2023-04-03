package organizations.domain.entities

class Organization(
    val id: Long,
    val name: String,
    val city: City
) {
    data class New(
        val name: String,
        val cityId: Long
    )
}
