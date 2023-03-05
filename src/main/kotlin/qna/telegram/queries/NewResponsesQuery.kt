package qna.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object NewResponsesQuery {
    @Serializable
    @SerialName("nrs")
    data class SeeNew(val questionId: Long, val doEdit: Boolean = true) : Query

    @Serializable
    @SerialName("nra")
    data class Accept(val responseId: Long, val questionId: Long) : Query
}
