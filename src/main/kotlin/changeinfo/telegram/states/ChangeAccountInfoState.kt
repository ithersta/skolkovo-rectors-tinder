package changeinfo.telegram.states

import auth.domain.entities.OrganizationType
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
object WaitingForNewNameState : DialogState

@Serializable
object WaitingForCityState : DialogState

@Serializable
class ChangeCityState(
    val city: Long
) : DialogState

@Serializable
object WaitingForProfessionState : DialogState

@Serializable
object WaitingForOrganizationTypeState : DialogState

@Serializable
data class ChangeOrganizationTypeState(val type: OrganizationType) : DialogState

@Serializable
class WaitingForOrganizationState(
    val cityId: Long

) : DialogState {
    fun next(organization: Long) = ChangeOrganizationState(organization)
}

@Serializable
class ChangeOrganizationState(
    val organizationId: Long

) : DialogState

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
