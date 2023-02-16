package mute.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("OnOffMute")
class OnOffMuteQuery(
    val on: Boolean
) : Query