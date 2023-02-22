package auth.telegram.states

import auth.domain.entities.PhoneNumber
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
object WaitingForContact : DialogState

@Serializable
data class WriteNameState(
    val phoneNumber: PhoneNumber
) : DialogState

@Serializable
data class ChooseCountry(
    val phoneNumber: PhoneNumber,
    val name: String
) : DialogState

@Serializable
data class ChooseCityInCIS(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String
) : DialogState

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
) : DialogState

@Serializable
data class WriteOrganizationState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String
) : DialogState

@Serializable
data class ChooseProfessionalAreasState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val messageId: MessageId? = null
) : DialogState

@Serializable
data class AddProfessionalAreasState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val messageId: MessageId? = null
) : DialogState

@Serializable
data class WriteProfessionalDescriptionState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>
) : DialogState

@Serializable
data class ChooseQuestionAreasState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val professionalDescription: String,
    val questionAreas: Set<QuestionArea>,
    val messageId: MessageId? = null
) : DialogState

@Serializable
data class AddAccountInfoToDataBaseState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val professionalDescription: String,
    val questionAreas: Set<QuestionArea>
) : DialogState
