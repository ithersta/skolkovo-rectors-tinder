package qna.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("a")
class AcceptQuestionQuery(val questionId: Long) : Query

@Serializable
@SerialName("d")
object DeclineQuestionQuery : Query
