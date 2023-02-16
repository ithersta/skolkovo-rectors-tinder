package mute.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("weekMonthMute")
class WeekMonthMuteQuery(
    val week: Boolean
) : Query
