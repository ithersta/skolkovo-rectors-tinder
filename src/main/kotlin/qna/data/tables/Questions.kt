package qna.data.tables

import auth.data.tables.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import qna.domain.entities.QuestionIntent

object Questions : LongIdTable() {
    val authorId: Column<EntityID<Long>> = reference("author_id", Users).index()
    val intent: Column<QuestionIntent> = enumeration("intent")
    val subject: Column<String> = varchar("subject", length = 256)
    val text: Column<String> = varchar("text", length = 2048)
    val isClosed: Column<Boolean> = bool("is_closed")
}
