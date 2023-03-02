package qna.domain.entities

data class Response(
    val id: Long,
    val questionId: Long,
    val respondentId: Long
)
