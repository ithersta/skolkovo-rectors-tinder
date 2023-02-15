package qna.states

import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent

@Serializable
class ChooseQuestionAreas(
    val question: String,
    val areas: Set<QuestionArea>,
    val messageId: MessageId? = null
) : DialogState

class ChooseQuestionIntent(
    val question: String,
    val areas: Set<QuestionArea>
): DialogState

class SendQuestionToCommunity(
    val question: String,
    val areas: Set<QuestionArea>,
    val intent: QuestionIntent
): DialogState
