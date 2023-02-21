package qna.telegram.queries

import common.telegram.Query
import dev.inmo.tgbotapi.types.UserId
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
class AcceptUserQuery(val userId: Long) : Query

@Serializable
@SerialName("de")
class DeclineUserQuery(val userId: Long) : Query
