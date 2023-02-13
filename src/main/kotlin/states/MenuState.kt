package states

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
    object Notifications : DialogState

    @Serializable
    object ChangeAccountInfo : DialogState
}
