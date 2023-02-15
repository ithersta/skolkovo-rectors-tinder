package feedback.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object FeedbackQuery {
    @Serializable
    @SerialName("sf")
    class SendFeedback(
        val responseId: Long,
        val isSuccessful: Boolean
    ) : Query
}
