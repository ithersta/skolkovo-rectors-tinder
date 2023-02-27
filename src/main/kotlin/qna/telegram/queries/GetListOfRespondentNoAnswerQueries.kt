package qna.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
@SerialName("su")
class SelectUserArea(val area: QuestionArea) : Query

@Serializable
@SerialName("ss")
class SelectSubject(val questionId: Long) : Query

@Serializable
@SerialName("sr")
class SelectRespondent(val respondentId: Long) : Query
