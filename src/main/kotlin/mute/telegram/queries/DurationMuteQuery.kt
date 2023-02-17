package mute.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@SerialName("weekMonthMute")
class DurationMuteQuery(
    val duration: Duration
) : Query
