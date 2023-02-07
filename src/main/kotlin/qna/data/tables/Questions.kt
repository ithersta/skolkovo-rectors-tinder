package qna.data.tables

import auth.data.tables.Users
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import qna.domain.entities.QuestionArea

object Questions : LongIdTable() {
    val authorId: Column<EntityID<Long>> = reference("author_id", Users).index()
    val subject: Column<String> = varchar("subject", length = 256)
    val text: Column<String> = varchar("text", length = 1024)
    val at: Column<Instant> = timestamp("at")
    val closedAt: Column<Instant?> = timestamp("closed_at").nullable()
}
