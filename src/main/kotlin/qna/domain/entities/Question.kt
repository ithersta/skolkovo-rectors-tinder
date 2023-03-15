package qna.domain.entities

import kotlinx.datetime.Instant

data class Question(
    val authorId: Long,
    val intent: QuestionIntent,
    val subject: String,
    val text: String,
    val isClosed: Boolean,
    val areas: Set<QuestionArea>,
    val at: Instant,
    val isBlockedCity: Boolean,
    val id: Long? = null
)
