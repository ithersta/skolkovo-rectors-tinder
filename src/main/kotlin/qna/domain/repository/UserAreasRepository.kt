package qna.domain.repository

import qna.domain.entities.Question
import qna.domain.entities.QuestionArea

interface UserAreasRepository {
    fun getUsersByArea(questionArea: QuestionArea): List<Long>
    fun getByArea(userId: Long, questionArea: QuestionArea): List<Question>
}
