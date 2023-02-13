package auth.domain.repository

import qna.domain.entities.QuestionArea

interface UserAreasRepository {
    fun getAllByArea(questionArea: QuestionArea): List<Long>
}