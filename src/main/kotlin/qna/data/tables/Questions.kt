package qna.data.tables

import auth.data.tables.Users
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import qna.domain.entities.HideFrom
import qna.domain.entities.Question
import qna.domain.entities.QuestionIntent

object Questions : LongIdTable() {
    val authorId: Column<EntityID<Long>> = reference("author_id", Users, onDelete = ReferenceOption.CASCADE).index()
    val intent: Column<QuestionIntent> = enumeration("intent")
    val subject: Column<String> = varchar("subject", length = Question.Subject.maxLength)
    val text: Column<String> = varchar("text", length = Question.Text.maxLength)
    val isClosed: Column<Boolean> = bool("is_closed")
    val at: Column<Instant> = timestamp("at")
    val hideFrom: Column<HideFrom> = enumeration<HideFrom>("hide_from").default(HideFrom.NoOne)

    init {
        index(isUnique = false, isClosed, at)
    }
}
