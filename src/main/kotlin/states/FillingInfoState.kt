package states

import dev.inmo.tgbotapi.types.MessageId
import fillingAccountInfo.QuestionArea
import kotlinx.serialization.Serializable

@Serializable
object WriteNameState: DialogState
@Serializable
class ChooseCityState(
    val name:String
) : DialogState

@Serializable
class WriteProfessionState(
    val name:String,
    val city: String
):DialogState

@Serializable
class WriteOrganizationState(
    val name:String,
    val city: String,
    val profession: String
):DialogState

@Serializable
class ChooseProfessionalAreasState(
    val name:String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val messageId:MessageId? = null
):DialogState

@Serializable
class AddProfessionalAreasState(///TODO: понадобится, когда список компетенций дадут и надо будет реализовать "Другое"
    val name:String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val messageId:MessageId? = null
):DialogState
@Serializable
class WriteProfessionalDescriptionState(
    val name:String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>
):DialogState

@Serializable
class ChooseQuestionAreasState(
    val name:String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val professionalDescription: String,
    val questionAreas: List<QuestionArea>,
    val messageId: MessageId? = null
):DialogState

@Serializable
class AddAccountInfoToDataBaseState(
    val name:String,
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val professionalDescription: String,
    val questionAreas: List<QuestionArea>
):DialogState
