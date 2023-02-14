package feedback.data.repository

import auth.data.tables.Users
import feedback.domain.entities.FeedbackRequest
import feedback.domain.repository.FeedbackRepository
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import qna.data.tables.Responses

@Single
class FeedbackRepositoryImpl : FeedbackRepository {
    override fun getFeedbackRequests(atUntil: Instant): List<FeedbackRequest> {
        return (Responses innerJoin Users)
            .select { (Responses.at less atUntil) and (Responses.askedForFeedback eq false) and (Responses.isSuccessful eq null) }
            .map {
                FeedbackRequest(
                    responseId = it[Responses.id].value,
                    respondentName = it[Users.name]
                )
            }
    }

    override fun markAsAsked(responseId: Long) {
        Responses.update({ Responses.id eq responseId }) {
            it[askedForFeedback] = true
        }
    }
}
