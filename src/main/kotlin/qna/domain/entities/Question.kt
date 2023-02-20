package qna.domain.entities

sealed interface Question {
    data class Details(
        val authorId: Long,
        val intent: QuestionIntent,
        val subject: String,
        val text: String,
        val isClosed: Boolean,
        val areas: Set<QuestionArea>
    )
}
