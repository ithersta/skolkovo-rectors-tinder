package event.telegram.states

import common.telegram.DialogState
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class InputBeginDateTimeState(
    val name: String
) : DialogState

@Serializable
data class InputEndDateTimeState(
    val name: String,
    val beginDateTime: Instant
) : DialogState

@Serializable
data class InputDescriptionState(
    val name: String,
    val beginDateTime: Instant,
    val endDateTime: Instant
) : DialogState

@Serializable
data class InputUrlState(
    val name: String,
    val beginDateTime: Instant,
    val endDateTime: Instant,
    val description: String? = null
) : DialogState

@Serializable
data class AskUserToCreateEvent(
    val name: String,
    val beginDateTime: Instant,
    val endDateTime: Instant,
    val description: String? = null,
    val url: String
) : DialogState
