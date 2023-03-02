package auth.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
@SerialName("sq")
class SelectQuestionQuery(val area: QuestionArea) : Query

@Serializable
@SerialName("uq")
class UnselectQuestionQuery(val area: QuestionArea) : Query

@Serializable
@SerialName("fq")
object FinishQuestionQuery : Query

@Serializable
@SerialName("country")
class SelectCountryQuery(val country: String) : Query

@Serializable
@SerialName("cis")
class SelectCityInCIS(val city: String) : Query

@Serializable
@SerialName("area")
class SelectDistrict(val district: String) : Query

@Serializable
@SerialName("r")
class SelectRegion(val region: String) : Query

@Serializable
@SerialName("city")
class SelectCity(val city: String) : Query

@Serializable
@SerialName("subject")
class SelectSubject(val questionId: Long) : Query

@Serializable
@SerialName("pN")
class SelectRespondent(val name: String, val phoneNumber: String) : Query
