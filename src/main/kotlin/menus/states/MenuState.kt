package menus.states

import com.ithersta.tgbotapi.pagination.PagerState
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
    object Notifications : DialogState

    @Serializable
    object ChangeAccountInfo : DialogState

    @Serializable
    object Events : DialogState

    @Serializable
    object CurrentIssues : DialogState

    @Serializable
    data class NextStep(
        val userId: Long,
        val areaIndex: Int,
        val pagerState: PagerState
    ) : DialogState

    @Serializable
    data class AnswerUser(
        val subject: String,
        val question: String,
        val areas: Set<QuestionArea>,
        val intent: QuestionIntent
    ) : DialogState
}
