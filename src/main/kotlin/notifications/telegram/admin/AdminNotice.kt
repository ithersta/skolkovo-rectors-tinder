package notifications.telegram.admin

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object AdminNotice {
    @Serializable
    @SerialName("aay")
    object AdminAnswerYes : Query

    @Serializable
    @SerialName("aaN")
    object AdminAnswerNo : Query
}