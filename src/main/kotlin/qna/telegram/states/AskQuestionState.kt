package qna.telegram.states

import common.telegram.DialogState
import dev.inmo.tgbotapi.types.MessageId
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent

@Serializable
data class AskFullQuestion(
    val subject: String
) : DialogState

@Serializable
data class ChooseQuestionAreas(
    val subject: String,
    val question: String,
    val areas: Set<QuestionArea>,
    val messageId: MessageId? = null
) : DialogState

@Serializable
data class ChooseQuestionIntent(
    val subject: String,
    val question: String,
    val areas: Set<QuestionArea>
) : DialogState

@Serializable
data class SendQuestionToCommunity(
    val subject: String,
    val question: String,
    val areas: Set<QuestionArea>,
    val intent: QuestionIntent
) : DialogState

@Serializable
data class SendQuestionToCurator(
    val subject: String,
    val question: String
) : DialogState
