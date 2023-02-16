package auth.domain.entities

import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent

sealed interface Question {
    data class Details(
        val authorId: Long,
        val intent: QuestionIntent,
        val subject: String,
        val text: String,
        val areas: Set<QuestionArea>
    )
}
