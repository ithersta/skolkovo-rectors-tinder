package qna.domain.entities

data class Question(
    val id: Long,
    val authorId: Long,
    val intent: QuestionIntent,
    val subject: String,
    val text: String,
    val isClosed: Boolean
)
