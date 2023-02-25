package auth.telegram.states

import auth.domain.entities.PhoneNumber
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
object WaitingForContact : DialogState {
    fun next(phoneNumber: PhoneNumber) = WriteNameState(phoneNumber)
}

@Serializable
data class WriteNameState(
    val phoneNumber: PhoneNumber
) : DialogState {
    fun next(name: String) = ChooseCountry(phoneNumber, name)
}

@Serializable
data class ChooseCountry(
    val phoneNumber: PhoneNumber,
    val name: String
) : DialogState {
    fun chooseDistrict() = ChooseDistrict(phoneNumber, name)
    fun chooseCityInCis(country: String) = ChooseCityInCis(phoneNumber, name, country)
}

@Serializable
data class ChooseCityInCis(
    val phoneNumber: PhoneNumber,
    val name: String,
    val country: String
) : DialogState {
    fun next(city: String) = WriteProfessionState(phoneNumber, name, city)
}

@Serializable
data class ChooseDistrict(
    val phoneNumber: PhoneNumber,
    val name: String
) : DialogState

@Serializable
data class ChooseRegion(
    val phoneNumber: PhoneNumber,
    val name: String,
    val district: String
) : DialogState

@Serializable
data class ChooseCity(
    val phoneNumber: PhoneNumber,
    val name: String,
    val region: String
) : DialogState

@Serializable
data class WriteProfessionState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String
) : DialogState {
    fun next(profession: String) = WriteOrganizationState(phoneNumber, name, city, profession)
}

@Serializable
data class WriteOrganizationState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String
) : DialogState {
    fun next(organization: String) =
        WriteProfessionalDescriptionState(phoneNumber, name, city, profession, organization)
}

@Serializable
data class WriteProfessionalDescriptionState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String
) : DialogState {
    fun next(professionalDescription: String) =
        ChooseQuestionAreasState(phoneNumber, name, city, profession, organization, professionalDescription)
}

@Serializable
data class ChooseQuestionAreasState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalDescription: String,
    val questionAreas: Set<QuestionArea> = emptySet(),
    val messageId: MessageId? = null
) : DialogState {
    fun next() = AddAccountInfoToDataBaseState(
        phoneNumber,
        name,
        city,
        profession,
        organization,
        professionalDescription,
        questionAreas
    )
}

@Serializable
data class AddAccountInfoToDataBaseState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalDescription: String,
    val questionAreas: Set<QuestionArea>
) : DialogState
