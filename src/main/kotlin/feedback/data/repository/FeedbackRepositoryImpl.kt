package feedback.data.repository

import auth.data.tables.Users
import feedback.domain.entities.FeedbackRequest
import feedback.domain.repository.FeedbackRepository
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import qna.data.tables.Questions
import qna.data.tables.Responses

@Single
class FeedbackRepositoryImpl : FeedbackRepository {
    override fun getFeedbackRequests(atUntil: Instant): List<FeedbackRequest> {
        return (Responses innerJoin Users innerJoin Questions)
            .select { (Responses.at less atUntil) and (Responses.askedForFeedback eq false) and (Responses.isSuccessful eq null) }
            .map {
                FeedbackRequest(
                    responseId = it[Responses.id].value,
                    respondentName = it[Users.name],
                    questionAuthorUserId = it[Questions.authorId].value
                )
            }
    }

    override fun getAuthorId(responseId: Long): Long? {
        return (Responses innerJoin Questions)
            .slice(Questions.authorId)
            .select { Responses.id eq responseId }
            .firstOrNull()?.get(Questions.authorId)?.value
    }

    override fun markAsAsked(responseId: Long) {
        Responses.update({ Responses.id eq responseId }) {
            it[askedForFeedback] = true
        }
    }

    override fun setFeedback(responseId: Long, isSuccessful: Boolean) {
        Responses.update({ Responses.id eq responseId }) {
            it[Responses.isSuccessful] = isSuccessful
        }
    }
}
