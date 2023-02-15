package qna.states

import common.telegram.DialogState
import kotlinx.serialization.Serializable

@Serializable
object SendingQuestionToCommunity : DialogState

@Serializable
object CommunicateWithUser : DialogState
