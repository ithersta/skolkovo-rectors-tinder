package event.telegram.states

import common.telegram.DialogState
import event.telegram.serializers.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class InputBeginDateTimeState(
    val name: String
) : DialogState

@Serializable
data class InputEndDateTimeState(
    val name: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val beginDateTime: OffsetDateTime
) : DialogState

@Serializable
data class InputDescriptionState(
    val name: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val beginDateTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endDateTime: OffsetDateTime
) : DialogState

@Serializable
data class InputUrlState(
    val name: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val beginDateTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endDateTime: OffsetDateTime,
    val description: String = ""
) : DialogState

data class AskUserToCreateEvent(
    val name: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val beginDateTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endDateTime: OffsetDateTime,
    val description: String = "",
    val url: String
) : DialogState
