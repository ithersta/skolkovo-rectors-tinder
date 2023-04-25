package qna.domain.entities

import common.domain.LimitedLengthStringType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

data class Question(
    val authorId: Long,
    val intent: QuestionIntent,
    val subject: Subject,
    val text: Text,
    val isClosed: Boolean,
    val areas: Set<QuestionArea>,
    val at: Instant,
    val hideFrom: HideFrom,
    val id: Long? = null
) {
    @Serializable
    @JvmInline
    value class Subject private constructor(val value: String) {
        companion object : LimitedLengthStringType<Subject>(maxLength = 256, { Subject(it) })
    }

    @Serializable
    @JvmInline
    value class Text private constructor(val value: String) {
        companion object : LimitedLengthStringType<Text>(maxLength = 2048, { Text(it) })
    }
}
