package qna.data.tables

import auth.data.tables.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object Responses : LongIdTable() {
    val questionId: Column<EntityID<Long>> = reference("question_id", Questions).index()
    val respondentId: Column<EntityID<Long>> = reference("respondent_id", Users).index()
    init {
        uniqueIndex(questionId, respondentId)
    }
}
