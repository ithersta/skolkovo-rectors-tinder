package common.telegram

import kotlinx.serialization.Serializable

interface DialogState {
    @Serializable
    object Empty : DialogState
}
