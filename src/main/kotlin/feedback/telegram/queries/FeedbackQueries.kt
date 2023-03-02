package feedback.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object FeedbackQueries {
    @Serializable
    @SerialName("sf")
    class SendFeedback(
        val responseId: Long,
        val isSuccessful: Boolean
    ) : Query

    @Serializable
    @SerialName("cq")
    class CloseQuestion(
        val questionId: Long
    ) : Query

    @Serializable
    @SerialName("lq")
    object DoNotCloseQuestion : Query
}
