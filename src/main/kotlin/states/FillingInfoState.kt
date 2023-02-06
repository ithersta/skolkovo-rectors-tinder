package states

import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import kotlinx.serialization.Serializable

@Serializable
object ChooseCityState : DialogState


@Serializable
class WriteProfessionState(
    val city: String
):DialogState

@Serializable
class WriteOrganizationState(
    val city: String,
    val profession: String
):DialogState

@Serializable
class WriteProfessionalActivityState(
    val city: String,
    val profession: String,
    val organization: String
):DialogState

@Serializable
class ChooseProfessionalAreasState(
    val city: String,
    val profession: String,
    val organization: String,
    val activityDescription: String
):DialogState