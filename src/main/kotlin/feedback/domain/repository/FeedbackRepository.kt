package feedback.domain.repository

import feedback.domain.entities.FeedbackRequest
import kotlinx.datetime.Instant

interface FeedbackRepository {
    fun getFeedbackRequests(atUntil: Instant): List<FeedbackRequest>
    fun markAsAsked(responseId: Long)
}
