package auth.telegram.states

import auth.domain.entities.PhoneNumber
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
object WaitingForContact : DialogState

@Serializable
class WriteNameState(
    val phoneNumber: PhoneNumber
) : DialogState

@Serializable
class ChooseCountry(
    val phoneNumber: PhoneNumber,
    val name: String
) : DialogState

@Serializable
class ChooseCityInCIS(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String
) : DialogState

@Serializable
class ChooseDistrict(
    val phoneNumber: PhoneNumber,
    val name: String,
    val county: String
) : DialogState

@Serializable
class ChooseRegion(
    val phoneNumber: PhoneNumber,
    val name: String,
    val district: String
) : DialogState

@Serializable
class ChooseCity(
    val phoneNumber: PhoneNumber,
    val name: String,
    val region: String
) : DialogState

@Serializable
class WriteProfessionState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String
) : DialogState

@Serializable
class WriteOrganizationState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String
) : DialogState

@Serializable
class ChooseProfessionalAreasState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val messageId: MessageId? = null
) : DialogState

@Serializable
class AddProfessionalAreasState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val messageId: MessageId? = null
) : DialogState

@Serializable
class WriteProfessionalDescriptionState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>
) : DialogState

@Serializable
class ChooseQuestionAreasState(
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
class AddAccountInfoToDataBaseState(
    val phoneNumber: PhoneNumber,
    val name: String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val professionalDescription: String,
    val questionAreas: Set<QuestionArea>
) : DialogState
