package qna.states

import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent

@Serializable
class AskFullQuestion(
    val subject: String
) : DialogState

@Serializable
class ChooseQuestionAreas(
    val subject: String,
    val question: String,
    val areas: Set<QuestionArea>,
    val messageId: MessageId? = null
) : DialogState

@Serializable
class ChooseQuestionIntent(
    val subject: String,
    val question: String,
    val areas: Set<QuestionArea>
): DialogState

@Serializable
class SendQuestionToCommunity(
    val subject: String,
    val question: String,
    val areas: Set<QuestionArea>,
    val intent: QuestionIntent
): DialogState
