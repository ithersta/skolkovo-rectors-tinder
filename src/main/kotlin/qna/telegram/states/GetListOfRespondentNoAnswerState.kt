package qna.telegram.states

import common.telegram.DialogState
import kotlinx.serialization.Serializable

@Serializable
data class GetListOfSubjects(
    val userId: Long,
) : DialogState

@Serializable
data class ChooseAction(
    val userId: Long,
    val questionId: Long
) : DialogState
