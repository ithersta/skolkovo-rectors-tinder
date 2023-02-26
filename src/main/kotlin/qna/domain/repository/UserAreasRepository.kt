package qna.domain.repository

import qna.domain.entities.QuestionArea

interface UserAreasRepository {
    fun getUsersByArea(questionArea: QuestionArea): List<Long>
    fun getSubjectsByArea(userId: Long, questionArea: Int): Map<Long, String>
}
