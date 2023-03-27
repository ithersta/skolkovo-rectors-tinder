package qna.domain.repository

import qna.domain.entities.Question
import qna.domain.entities.QuestionArea

interface UserAreasRepository {
    fun getQuestionsByUserIdAndUserArea(userId: Long, userArea: QuestionArea): List<Question>
}
