package changeAccountInfo.telegram.states

import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
object WaitingForNewName : DialogState

@Serializable
object WaitingForCountry: DialogState  {
    fun chooseDistrict() = WaitingForDistrict
    fun chooseCityInCis(country: String) = WaitingForCityInCis(country)
}

@Serializable
class WaitingForCityInCis(
    val country: String
) : DialogState

@Serializable
object WaitingForDistrict : DialogState

@Serializable
class WaitingForRegion(
    val district: String
) : DialogState

@Serializable
class WaitingForCity(
    val region: String
) : DialogState

@Serializable
object WaitingForProfessionState: DialogState

@Serializable
object WaitingForOrganizationState : DialogState

@Serializable
object WaitingForProfessionalDescriptionState : DialogState

@Serializable
data class WaitingForQuestionAreasState(
    val questionAreas: Set<QuestionArea> = emptySet(),
    val messageId: MessageId? = null
) : DialogState