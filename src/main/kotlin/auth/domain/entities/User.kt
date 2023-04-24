package auth.domain.entities

import common.domain.LimitedStringCompanion
import kotlinx.serialization.Serializable
import organizations.domain.entities.City
import organizations.domain.entities.Organization
import qna.domain.entities.QuestionArea

sealed interface User {
    object Unauthenticated : User
    object Unapproved : User
    class Admin(id: Long) : Normal(id)
    open class Normal(val id: Long) : User

    data class Details(
        val id: Long,
        val phoneNumber: PhoneNumber,
        val course: Course,
        val name: Name,
        val job: Job,
        val city: City,
        val organizationType: OrganizationType,
        val organization: Organization,
        val activityDescription: ActivityDescription,
        val isApproved: Boolean,
        val areas: Set<QuestionArea>
    )

    data class NewDetails(
        val id: Long,
        val phoneNumber: PhoneNumber,
        val course: Course,
        val name: Name,
        val job: Job,
        val cityId: Long,
        val organizationType: OrganizationType,
        val organizationId: Long,
        val activityDescription: ActivityDescription,
        val areas: Set<QuestionArea>
    )

    @Serializable
    @JvmInline
    value class Name private constructor(val value: String) {
        companion object : LimitedStringCompanion<Name>(maxLength = 256, { Name(it) })
    }

    @Serializable
    @JvmInline
    value class Job private constructor(val value: String) {
        companion object : LimitedStringCompanion<Job>(maxLength = 256, { Job(it) })
    }

    @Serializable
    @JvmInline
    value class ActivityDescription private constructor(val value: String) {
        companion object : LimitedStringCompanion<ActivityDescription>(maxLength = 1024, { ActivityDescription(it) })
    }
}
