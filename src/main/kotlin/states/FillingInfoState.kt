package states

import dev.inmo.tgbotapi.types.MessageId
import fillingAccountInfo.QuestionArea
import kotlinx.serialization.Serializable

@Serializable
object ChooseCityState : DialogState

@Serializable
class WriteProfessionState(
    val city: String
) : DialogState

@Serializable
class WriteOrganizationState(
    val city: String,
    val profession: String
) : DialogState

@Serializable
class ChooseProfessionalAreasState(
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>
) : DialogState

@Serializable
class AddProfessionalAreasState( // /TODO: понадобится, когда список компетенций дадут и надо будет реализовать "Другое"
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>
) : DialogState

@Serializable
class WriteProfessionalDescriptionState(
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>
) : DialogState

@Serializable
class ChooseQuestionAreasState(
    val city: String,
    val profession: String,
    val organization: String,
    val professionalAreas: List<String>,
    val professionalDescription: String,
    val questionAreas: List<QuestionArea>,
    val messageId: MessageId? = null
) : DialogState
