package auth.telegram.states

import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
object WaitingForContact : DialogState {
    fun next(phoneNumber: PhoneNumber) = ChooseCourseState(phoneNumber)
}

@Serializable
data class ChooseCourseState(
    val phoneNumber: PhoneNumber
) : DialogState {
    fun next(course: Course) = WriteNameState(phoneNumber, course)
}

@Serializable
data class WriteNameState(
    val phoneNumber: PhoneNumber,
    val course: Course
) : DialogState {
    fun next(name: User.Name) = ChooseCity(phoneNumber, course, name)
}

@Serializable
data class ChooseCity(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: User.Name
) : DialogState {
    fun next(cityId: Long) = WriteJobState(phoneNumber, course, name, cityId)
}

@Serializable
data class WriteJobState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: User.Name,
    val cityId: Long
) : DialogState {
    fun next(job: User.Job) = ChooseOrganizationTypeState(phoneNumber, course, name, cityId, job)
}

@Serializable
data class ChooseOrganizationTypeState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: User.Name,
    val cityId: Long,
    val job: User.Job
) : DialogState {
    fun next(organizationType: OrganizationType) =
        WriteOrganizationState(phoneNumber, course, name, cityId, job, organizationType)
}

@Serializable
data class WriteOrganizationState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: User.Name,
    val cityId: Long,
    val job: User.Job,
    val organizationType: OrganizationType

) : DialogState {
    fun next(organization: Long) =
        WriteActivityDescriptionState(phoneNumber, course, name, cityId, job, organizationType, organization)
}

@Serializable
data class WriteActivityDescriptionState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: User.Name,
    val cityId: Long,
    val job: User.Job,
    val organizationType: OrganizationType,
    val organization: Long
) : DialogState {
    fun next(activityDescription: User.ActivityDescription) =
        ChooseQuestionAreasState(
            phoneNumber,
            course,
            name,
            cityId,
            job,
            organizationType,
            organization,
            activityDescription
        )
}

@Serializable
data class ChooseQuestionAreasState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: User.Name,
    val cityId: Long,
    val job: User.Job,
    val organizationType: OrganizationType,
    val organization: Long,
    val activityDescription: User.ActivityDescription,
    val questionAreas: Set<QuestionArea> = emptySet(),
    val messageId: MessageId? = null
) : DialogState {
    fun next() = AddAccountInfoToDataBaseState(
        phoneNumber, course, name, cityId,
        job, organizationType, organization, activityDescription, questionAreas
    )
}

@Serializable
data class AddAccountInfoToDataBaseState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: User.Name,
    val cityId: Long,
    val job: User.Job,
    val organizationType: OrganizationType,
    val organization: Long,
    val activityDescription: User.ActivityDescription,
    val questionAreas: Set<QuestionArea>
) : DialogState
