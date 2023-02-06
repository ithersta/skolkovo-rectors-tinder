package qna.data.tables

import auth.data.tables.Users
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Responses : LongIdTable() {
    val questionId: Column<EntityID<Long>> = reference("question_id", Questions).index()
    val respondentId: Column<EntityID<Long>> = reference("respondent_id", Users).index()
    val at: Column<Instant> = timestamp("at")
}
