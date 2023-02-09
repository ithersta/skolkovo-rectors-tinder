package states

import kotlinx.serialization.Serializable

@Serializable
sealed interface DialogState {
    @Serializable
    object Empty : DialogState
}