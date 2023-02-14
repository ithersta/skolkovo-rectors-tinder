package feedback.telegram.queries

import common.telegram.Query
import kotlinx.serialization.Serializable

object FeedbackQuery {
    @Serializable
    class SendFeedback(
        val responseId: Long,
        val isSuccessful: Boolean
    ) : Query
}
