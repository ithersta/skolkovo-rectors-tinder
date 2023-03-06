package auth.telegram.states

import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
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
    fun next(name: String) = ChooseCity(phoneNumber, course, name)
}

@Serializable
data class ChooseCity(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: String
) : DialogState {
    fun next(city: String) = WriteProfessionState(phoneNumber, course, name, city)
}

@Serializable
data class WriteProfessionState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: String,
    val city: String
) : DialogState {
    fun next(profession: String) = ChooseOrganizationTypeState(phoneNumber, course, name, city, profession)
}

@Serializable
data class ChooseOrganizationTypeState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: String,
    val city: String,
    val profession: String
) : DialogState {
    fun next(organizationType: OrganizationType) =
        WriteOrganizationState(phoneNumber, course, name, city, profession, organizationType)
}

@Serializable
data class WriteOrganizationState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: String,
    val city: String,
    val profession: String,
    val organizationType: OrganizationType

) : DialogState {
    fun next(organization: String) =
        WriteProfessionalDescriptionState(phoneNumber, course, name, city, profession, organizationType, organization)
}

@Serializable
data class WriteProfessionalDescriptionState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: String,
    val city: String,
    val profession: String,
    val organizationType: OrganizationType,
    val organization: String
) : DialogState {
    fun next(professionalDescription: String) =
        ChooseQuestionAreasState(
            phoneNumber,
            course,
            name,
            city,
            profession,
            organizationType,
            organization,
            professionalDescription)
}

@Serializable
data class ChooseQuestionAreasState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: String,
    val city: String,
    val profession: String,
    val organizationType: OrganizationType,
    val organization: String,
    val professionalDescription: String,
    val questionAreas: Set<QuestionArea> = emptySet(),
    val messageId: MessageId? = null
) : DialogState {
    fun next() = AddAccountInfoToDataBaseState(phoneNumber, course, name, city,
        profession, organizationType, organization, professionalDescription, questionAreas)
}

@Serializable
data class AddAccountInfoToDataBaseState(
    val phoneNumber: PhoneNumber,
    val course: Course,
    val name: String,
    val city: String,
    val profession: String,
    val organizationType: OrganizationType,
    val organization: String,
    val professionalDescription: String,
    val questionAreas: Set<QuestionArea>
) : DialogState
