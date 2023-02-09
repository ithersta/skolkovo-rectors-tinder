package queries

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("s")
class SelectQuery(val area: String) : Query

@Serializable
@SerialName("u")
class UnselectQuery(val area: String) : Query

@Serializable
@SerialName("f")
object FinishQuery : Query