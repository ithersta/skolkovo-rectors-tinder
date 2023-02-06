package auth.domain.entities

sealed interface User {
    object Unauthenticated : User
    object Admin : User
    data class Normal(val details: Details) : User

    data class Details(
        val id: Long,
        val phoneNumber: PhoneNumber,
        val city: String,
        val job: String,
        val organization: String,
        val professionalAreas: String,
        val activityDescription: String
    )
}
