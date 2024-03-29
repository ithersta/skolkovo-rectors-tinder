package qna.domain.repository

import common.domain.Paginated
import kotlinx.datetime.Instant
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea

interface QuestionRepository {
    fun close(questionId: Long)
    fun closeOlderThan(instant: Instant)
    fun add(question: Question): Long
    fun getById(questionId: Long): Question?
    fun getWithUnsentResponses(): List<Question>
    fun getClosed(authorId: Long): List<Question>
    fun getNotifiableUserIds(questionId: Long): List<Long>
    fun getQuestionsDigestPaginated(
        from: Instant,
        until: Instant,
        viewerUserId: Long,
        area: QuestionArea?,
        limit: Int,
        offset: Int
    ): Paginated<Question>
    fun getByUserId(userId: Long, offset: Int, limit: Int): Paginated<Question>
}
