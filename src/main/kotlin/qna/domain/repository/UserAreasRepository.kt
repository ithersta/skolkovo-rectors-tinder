package qna.domain.repository

import qna.domain.entities.Question
import qna.domain.entities.QuestionArea

interface UserAreasRepository {
    fun getUsersByArea(questionArea: QuestionArea): List<Long>
    fun getFilteredUsersByArea(questionArea: QuestionArea, city: String): List<Long>
    fun getSubjectsByUserId(userId: Long, userArea: QuestionArea): List<Question>
}
