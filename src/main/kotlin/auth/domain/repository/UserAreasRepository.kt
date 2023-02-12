package auth.domain.repository

import qna.domain.entities.QuestionArea

interface UserAreasRepository {
    fun getAllByArea(areaQ: QuestionArea): List<Long>
}