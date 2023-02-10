package states

import common.telegram.DialogState
import kotlinx.serialization.Serializable

object MenuState {
    @Serializable
    object AddUser : DialogState

    @Serializable
    object Questions : DialogState

    @Serializable
    object Notifications : DialogState

    @Serializable
    object ChangeAccountInfo : DialogState
}
