package auth.domain.entities

import qna.domain.entities.QuestionArea

sealed interface User {
    object Unauthenticated : User
    class Admin(id: Long) : Normal(id)
    open class Normal(val id: Long) : User

    data class Details(
        val id: Long,
        val phoneNumber: PhoneNumber,
        val course: Course,
        val name: String,
        val city: String,
        val job: String,
        val organizationType: OrganizationType,
        val organization: String,
        val activityDescription: String,
        val areas: Set<QuestionArea>
    )
}
