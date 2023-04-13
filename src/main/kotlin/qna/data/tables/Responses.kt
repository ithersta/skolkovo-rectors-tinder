package qna.data.tables

import auth.data.tables.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object Responses : LongIdTable() {
    val questionId: Column<EntityID<Long>> = reference("question_id", Questions, onDelete = ReferenceOption.CASCADE).index()
    val respondentId: Column<EntityID<Long>> = reference("respondent_id", Users, onDelete = ReferenceOption.CASCADE).index()
    val hasBeenSent: Column<Boolean> = bool("has_been_sent").default(false).index()

    init {
        uniqueIndex(questionId, respondentId)
    }
}
