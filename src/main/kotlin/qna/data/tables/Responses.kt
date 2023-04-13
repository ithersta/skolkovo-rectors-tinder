package qna.data.tables

import auth.data.tables.Users
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Responses : LongIdTable() {
    val questionId = reference("question_id", Questions, onDelete = ReferenceOption.CASCADE).index()
    val respondentId = reference("respondent_id", Users, onDelete = ReferenceOption.CASCADE).index()
    val hasBeenSent = bool("has_been_sent").default(false).index()

    init {
        uniqueIndex(questionId, respondentId)
    }
}
