package adduniversity.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("add_city_admin")
class AddCityQuery(
    val userId: Long
) : Query

@Serializable
@SerialName("add_university_admin")
class AddUniversityQuery(
    val userId: Long
) : Query
