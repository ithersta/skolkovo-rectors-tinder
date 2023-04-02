package locations.domain.entities

sealed interface Location {
    val id: Long
    val name: String
    val isPhantom get() = name.isEmpty()

    sealed interface WithChildren : Location
    sealed interface WithParent : Location {
        val parentId: Long
    }

    data class Country(
        override val id: Long,
        override val name: String
    ) : Location, WithChildren

    data class District(
        override val id: Long,
        override val name: String,
        override val parentId: Long
    ) : Location, WithParent, WithChildren

    data class Region(
        override val id: Long,
        override val name: String,
        override val parentId: Long
    ) : Location, WithParent, WithChildren

    data class City(
        override val id: Long,
        override val name: String,
        override val parentId: Long
    ) : Location, WithParent
}
