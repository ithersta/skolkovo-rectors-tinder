package changeinfo.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("n")
object WaitingForNewName : Query

@Serializable
@SerialName("co")
object WaitingForCity : Query

@Serializable
@SerialName("prof")
object WaitingForProfession : Query

@Serializable
@SerialName("org")
object WaitingForOrganization : Query

@Serializable
@SerialName("desc")
object WaitingForProfessionalDescription : Query

@Serializable
@SerialName("ar")
object WaitingForQuestionAreas : Query
