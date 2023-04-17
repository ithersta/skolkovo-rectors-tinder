package notifications.telegram.admin

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object UserApprovalQueries {
    @Serializable
    @SerialName("aay")
    class Approve(val chatId: Long) : Query

    @Serializable
    @SerialName("aaN")
    class Disapprove(val chatId: Long) : Query
}
