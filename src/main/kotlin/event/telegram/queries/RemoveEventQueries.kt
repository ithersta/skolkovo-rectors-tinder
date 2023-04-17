package event.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("se")
class SelectEvent(val id: Long) : Query

@Serializable
@SerialName("de")
class DeleteEvent(val id: Long) : Query

@Serializable
@SerialName("nde")
object NotDeleteEvent: Query
