package auth.domain.entities

import organizations.domain.entities.Organization
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
        val job: String,
        val organizationType: OrganizationType,
        val organization: Organization,
        val activityDescription: String,
        val areas: Set<QuestionArea>
    )

    data class NewDetails(
        val id: Long,
        val phoneNumber: PhoneNumber,
        val course: Course,
        val name: String,
        val job: String,
        val organizationType: OrganizationType,
        val organizationId: Long,
        val activityDescription: String,
        val areas: Set<QuestionArea>
    )
}
