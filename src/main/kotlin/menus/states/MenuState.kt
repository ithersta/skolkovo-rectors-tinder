package menus.states

import common.telegram.DialogState
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent

object MenuState {
    @Serializable
    object AddUser : DialogState

    object Questions {
        @Serializable
        object Main : DialogState

        @Serializable
        object GetQuestion : DialogState

        @Serializable
        object GetMyQuestion : DialogState

        @Serializable
        object AskQuestion : DialogState
    }

    @Serializable
    object Events : DialogState

    @Serializable
    object CurrentIssues : DialogState


    @Serializable
    data class AnswerUser(
        val subject: String,
        val question: String,
        val areas: Set<QuestionArea>,
        val intent: QuestionIntent
    ) : DialogState

    @Serializable
    data class OldQuestion(val pagerState: PagerState) : DialogState
}
