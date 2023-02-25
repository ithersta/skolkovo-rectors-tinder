package qna.domain.entities

data class Question(
    val authorId: Long,
    val intent: QuestionIntent,
    val subject: String,
    val text: String,
    val isClosed: Boolean,
    val areas: Set<QuestionArea>,
    val id: Long? = null
)
