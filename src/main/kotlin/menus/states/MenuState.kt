package menus.states

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
    object AccountInfoState : DialogState

    @Serializable
    object AdminMenuState: DialogState

    @Serializable
    object AddCityState: DialogState

    @Serializable
    object AddUniversityState: DialogState

    @Serializable
    object AddEventState: DialogState

    @Serializable
    object Events : DialogState
}
