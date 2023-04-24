package addorganizations.telegram.states

import common.telegram.DialogState
import kotlinx.serialization.Serializable
import organizations.domain.entities.City

@Serializable
class AddCityUserState : DialogState

@Serializable
data class ChooseOrganizationInputState(val cityName: City.Name) : DialogState {
    fun next(organizationId: Long?): DialogState {
        return AddOrganizationCityUserState(cityName, organizationId)
    }
}

@Serializable
data class AddOrganizationCityUserState(
    val cityName: City.Name,
    val organizationId: Long?
) : DialogState

@Serializable
data class AddOrganizationUserState(
    val cityId: Long
) : DialogState

@Serializable
data class CheckCityAdminState(
    val userId: Long?
) : DialogState {
    fun next(): DialogState {
        return AddCityAdminState(userId)
    }
}

@Serializable
data class AddCityAdminState(
    val userId: Long?
) : DialogState

@Serializable
data class ChooseCityOrganizationAdminState(
    val userId: Long?
) : DialogState {
    fun next(cityId: Long): DialogState {
        return ChooseOrganizationAdminState(userId, cityId)
    }
}

@Serializable
data class ChooseOrganizationAdminState(
    val userId: Long?,
    val cityId: Long
) : DialogState {
    fun next(organizationId: Long?): DialogState {
        return AddOrganizationAdminState(userId, cityId, organizationId)
    }
}

@Serializable
data class AddOrganizationAdminState(
    val userId: Long?,
    val cityId: Long,
    val organizationId: Long?
) : DialogState
