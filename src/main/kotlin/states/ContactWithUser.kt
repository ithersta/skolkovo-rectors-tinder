package states

import kotlinx.serialization.Serializable

@Serializable
object AnswerToQuestion : DialogState

@Serializable
object CommunicateWithUser : DialogState

@Serializable
object NotAgreeToCommunicate : DialogState

@Serializable
object AgreeToCommunicate : DialogState
