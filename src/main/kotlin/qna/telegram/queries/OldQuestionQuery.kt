package qna.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("sub")
class SelectTopic(val questionId: Long) : Query

@Serializable
@SerialName("sor")
class SelectOldQuestionRespondent(val responseId: Long) : Query