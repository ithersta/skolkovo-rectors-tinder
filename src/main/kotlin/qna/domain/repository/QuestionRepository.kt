package qna.domain.repository

import common.domain.Paginated
import kotlinx.datetime.Instant
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea

interface QuestionRepository {
    fun close(questionId: Long)
    fun add(question: Question): Long
    fun getById(questionId: Long): Question?
    fun getQuestionsDigestPaginated(
        from: Instant,
        until: Instant,
        userId: Long,
        limit: Int,
        offset: Int
    ): Paginated<Question>
    fun getQuestionAreasByUserId(userId: Long): List<QuestionArea>
    fun getByArea(userId: Long, questionArea: QuestionArea): List<Question>
}
