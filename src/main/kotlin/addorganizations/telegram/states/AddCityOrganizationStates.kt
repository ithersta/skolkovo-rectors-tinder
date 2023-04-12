package addorganizations.telegram.states

import common.telegram.DialogState
import kotlinx.serialization.Serializable

@Serializable
class AddCityUserState() : DialogState

@Serializable
data class ChooseOrganizationInputState(val city: String) : DialogState {
    fun next(organizationId: Long?): DialogState {
        return AddOrganizationCityUserState(city, organizationId)
    }
}

@Serializable
data class AddOrganizationCityUserState(
    val city: String,
    val organizationId: Long?
) : DialogState

@Serializable
data class AddOrganizationUserState(
    val cityId: Long
) : DialogState
