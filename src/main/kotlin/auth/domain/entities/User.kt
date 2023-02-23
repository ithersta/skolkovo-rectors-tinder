package auth.domain.entities

import qna.domain.entities.QuestionArea

sealed interface User {
    object Unauthenticated : User
    object Admin : Normal()
    open class Normal : User

    data class Details(
        val id: Long,
        val phoneNumber: PhoneNumber,
        val name: String,
        val city: String,
        val job: String,
        val organization: String,
        val activityDescription: String,
        val areas: Set<QuestionArea>
    )
}
