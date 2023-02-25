package menus.states

import com.ithersta.tgbotapi.pagination.PagerState
import common.telegram.DialogState
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

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
        val area: QuestionArea,
        val pagerState: PagerState
    ) : DialogState
}
