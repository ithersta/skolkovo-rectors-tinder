package adduniversity.telegram.states

import common.telegram.DialogState
import kotlinx.serialization.Serializable

@Serializable
class AddCityState(): DialogState

@Serializable
class AddCityInUniversityState(): DialogState

@Serializable
data class AddUniversityState(
    val city: String
): DialogState
