package qna.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("su")
class SelectSubject(val userId: Long, val questionId: Long) : Query

@Serializable
@SerialName("cque")
class CloseQuestion(val userId: Long, val questionId: Long) : Query

@Serializable
@SerialName("sl")
class SeeList(val userId: Long, val questionId: Long) : Query

@Serializable
@SerialName("sr")
class SelectRespondent(val respondentId: Long, val questionId: Long) : Query
