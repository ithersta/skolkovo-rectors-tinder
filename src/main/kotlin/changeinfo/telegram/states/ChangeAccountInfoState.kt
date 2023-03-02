package changeinfo.telegram.states

import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
object WaitingForNewNameState : DialogState

@Serializable
data class WaitingForCityState(
    val city: String? = null
) : DialogState {
    fun next() = ChangeCityState(city!!)
}

@Serializable
class ChangeCityState(
    val city: String
) : DialogState

@Serializable
object WaitingForProfessionState : DialogState

@Serializable
object WaitingForOrganizationState : DialogState

@Serializable
object WaitingForProfessionalDescriptionState : DialogState

@Serializable
data class WaitingForQuestionAreasState(
    val questionAreas: Set<QuestionArea> = emptySet(),
    val messageId: MessageId? = null
) : DialogState

@Serializable
class ChangeQuestionAreaState(
    val questionAreas: Set<QuestionArea>
) : DialogState
