package feedback.data.tables

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import qna.data.tables.Responses

object Feedback : LongIdTable() {
    val responseId: Column<EntityID<Long>> = reference("response_id", Responses).index()
    val isSuccessful: Column<Boolean> = bool("is_successful")
    val at: Column<Instant> = timestamp("at")
}
