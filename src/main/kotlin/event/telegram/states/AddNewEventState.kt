package event.telegram.states

import common.telegram.DialogState
import event.domain.entities.Event
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class InputBeginDateTimeState(
    val name: Event.Name
) : DialogState

@Serializable
data class InputEndDateTimeState(
    val name: Event.Name,
    val beginDateTime: Instant
) : DialogState

@Serializable
data class InputDescriptionState(
    val name: Event.Name,
    val beginDateTime: Instant,
    val endDateTime: Instant
) : DialogState

@Serializable
data class InputUrlState(
    val name: Event.Name,
    val beginDateTime: Instant,
    val endDateTime: Instant,
    val description: Event.Description? = null
) : DialogState

@Serializable
data class AskUserToCreateEvent(
    val name: Event.Name,
    val beginDateTime: Instant,
    val endDateTime: Instant,
    val description: Event.Description? = null,
    val url: Event.Url
) : DialogState
