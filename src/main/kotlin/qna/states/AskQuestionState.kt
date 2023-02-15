package qna.states

import common.telegram.DialogState
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent

@Serializable
class ChooseOfQuestionAreas(
    val question: String
) : DialogState

class ChoosePurposeOfQuestion(
    val question: String,
    val areas: Set<QuestionArea>
): DialogState

class SendQuestionToCommunity(
    val question: String,
    val areas: Set<QuestionArea>,
    val intent: Set<QuestionIntent>
): DialogState
