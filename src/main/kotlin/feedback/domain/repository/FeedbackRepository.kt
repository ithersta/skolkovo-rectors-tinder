package feedback.domain.repository

import feedback.domain.entities.FeedbackRequest
import kotlinx.datetime.Instant

interface FeedbackRepository {
    fun getFeedbackRequests(atUntil: Instant): List<FeedbackRequest>
    fun getAuthorId(responseId: Long): Long?
    fun markAsAsked(responseId: Long)
    fun setFeedback(responseId: Long, isSuccessful: Boolean)
    fun closeAssociatedQuestion(responseId: Long)
}
