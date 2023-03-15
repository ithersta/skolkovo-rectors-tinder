package qna.domain.repository

import common.domain.Paginated
import kotlinx.datetime.Instant
import qna.domain.entities.Question

interface QuestionRepository {
    fun close(questionId: Long)
    fun closeOlderThan(instant: Instant)
    fun add(question: Question): Long
    fun getById(questionId: Long): Question?
    fun getWithUnsentResponses(): List<Question>
    fun getSubjectsByUserIdAndIsClosed(userId: Long): List<Question>
    fun getQuestionsDigestPaginated(
        from: Instant,
        until: Instant,
        userId: Long,
        limit: Int,
        offset: Int
    ): Paginated<Question>
    fun getByUserId(userId: Long, offset: Int, limit: Int): Paginated<Question>
}
