package auth.domain.repository

import qna.domain.entities.QuestionArea

interface UserAreasRepository {
    fun getUsersByArea(questionArea: QuestionArea, userId: Long): List<Long>
}