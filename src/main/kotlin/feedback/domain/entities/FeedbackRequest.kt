package feedback.domain.entities

class FeedbackRequest(
    val responseId: Long,
    val respondentName: String,
    val questionAuthorUserId: Long
)
