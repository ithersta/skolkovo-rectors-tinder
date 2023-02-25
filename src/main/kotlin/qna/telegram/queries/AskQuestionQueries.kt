package qna.telegram

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("a")
class AcceptQuestionQuery(val questionId: Long) : Query

@Serializable
@SerialName("d")
object DeclineQuestionQuery : Query

@Serializable
@SerialName("ac")
class AcceptUserQuery(val respondentId: Long, val questionId: Long, val responseId: Long) : Query

@Serializable
@SerialName("de")
class DeclineUserQuery(val userId: Long) : Query
