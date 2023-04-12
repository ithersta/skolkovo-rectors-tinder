package notifications.telegram.admin

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object AdminNotice {
    @Serializable
    @SerialName("aay")
    class AdminAnswerYes(val chatId: Long) : Query

    @Serializable
    @SerialName("aaN")
    class AdminAnswerNo(val chatId: Long) : Query
}
