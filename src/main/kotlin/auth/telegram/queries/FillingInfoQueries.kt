package auth.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
@SerialName("s")
class SelectQuery(val area: String) : Query

@Serializable
@SerialName("u")
class UnselectQuery(val area: String) : Query

@Serializable
@SerialName("f")
object FinishQuery : Query

@Serializable
@SerialName("sq")
class SelectQuestionQuery(val area: QuestionArea) : Query

@Serializable
@SerialName("uq")
class UnselectQuestionQuery(val area: QuestionArea) : Query

@Serializable
@SerialName("fq")
object FinishQuestionQuery : Query
