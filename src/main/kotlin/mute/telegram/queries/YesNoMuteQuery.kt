package mute.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("YesNoMute")
class YesNoMuteQuery(
    val yes: Boolean
) : Query
