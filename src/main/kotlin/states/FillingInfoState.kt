package states

import auth.domain.entities.PhoneNumber
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
class WriteNameState(
    val phoneNumber: PhoneNumber
) : DialogState

@Serializable
class ChooseCityState(
    val phoneNumber: PhoneNumber,
    val name: String
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
