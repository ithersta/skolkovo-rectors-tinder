package auth.telegram.queries

import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
@SerialName("course")
class ChooseCourseQuery(val course: Course) : Query

@Serializable
@SerialName("country")
class SelectCountryQuery(val country: String) : Query

@Serializable
@SerialName("cis")
class SelectCityInCISQuery(val city: String) : Query

@Serializable
@SerialName("area")
class SelectDistrictQuery(val district: String) : Query

@Serializable
@SerialName("r")
class SelectRegionQuery(val region: String) : Query

@Serializable
@SerialName("city")
class SelectCityQuery(val city: String) : Query

@Serializable
@SerialName("type")
class ChooseOrganizationTypeQuery(val type: OrganizationType) : Query

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
@SerialName("start")
object StartQuery : Query
