import kotlinx.serialization.Serializable

object MuteStates {
    @Serializable
    object StartMute : DialogState

    @Serializable
    object MutePause : DialogState
}
