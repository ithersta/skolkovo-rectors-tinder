package qna.domain.repository

import qna.domain.entities.QuestionArea

interface UserAreasRepository {
    fun getUsersByArea(questionArea: QuestionArea): List<Long>
    fun getSubjectsByUserId(userId: Long, userArea: QuestionArea): Map<Long, String>
}
