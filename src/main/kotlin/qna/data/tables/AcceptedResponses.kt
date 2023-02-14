package qna.data.tables

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object AcceptedResponses : Table() {
    val responseId: Column<EntityID<Long>> = reference("response_id", Responses)
    val at: Column<Instant> = timestamp("at")
    val isSuccessful: Column<Boolean?> = bool("is_successful").nullable().default(null)
    val didAskFeedback: Column<Boolean> = bool("did_ask_feedback").default(false)
    override val primaryKey = PrimaryKey(responseId)
}
