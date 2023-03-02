package menus.states

import com.ithersta.tgbotapi.pagination.PagerState
import common.telegram.DialogState
import kotlinx.serialization.Serializable

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
    data class OldQuestion(val pagerState: PagerState) : DialogState
}
