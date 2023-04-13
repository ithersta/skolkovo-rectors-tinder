package addorganizations.telegram.flows

import addorganizations.domain.usecases.*
import addorganizations.telegram.AddingStrings
import addorganizations.telegram.queries.AddCityQuery
import addorganizations.telegram.queries.AddOrganizationQuery
import addorganizations.telegram.states.*
import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.functions.selectCity
import common.telegram.functions.selectOrganization
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.toChatId
import generated.onDataCallbackQuery
import org.koin.core.component.inject

fun RoleFilterBuilder<DialogState, User, User.Admin, UserId>.addCityOrganizationAdminFlow() {
    val getCityByIdUseCase: GetCityByIdUseCase by inject()
    val getOrganizationByIdUseCase: GetOrganizationByIdUseCase by inject()
    val addCityUseCase: AddCityUseCase by inject()
    val addOrganizationCityUseCase: AddOrganizationCityUseCase by inject()
    val addOrganizationUseCase: AddOrganizationUseCase by inject()
    anyState {
        onDataCallbackQuery(AddCityQuery::class) { (data, message) ->
            state.override { CheckCityAdminState(data.userId) }
        }
        onDataCallbackQuery(AddOrganizationQuery::class) { (data, message) ->
            state.override { ChooseCityOrganizationAdminState(data.userId) }
        }
    }
    state<CheckCityAdminState> {
        selectCity(
            stringsCity = AddingStrings.CityDropdown,
            onFinish = { chatId, state, cityId ->
                val city = getCityByIdUseCase(cityId)!!
                sendTextMessage(
                    chatId,
                    AddingStrings.havingCityAdmin(city.name)
                )
                sendTextMessage(
                    state.userId.toChatId(),
                    AddingStrings.havingCityUser(city.name)
                )
                DialogState.Empty
            },
            onNone = { state -> state.next() }
        )
    }
    state<AddCityAdminState> {
        onEnter { user ->
            sendTextMessage(
                user,
                AddingStrings.InputCity,
                replyMarkup = ReplyKeyboardRemove()
            )
        }
        onText { message ->
            addCityUseCase(message.content.text)
            sendTextMessage(
                message.chat.id,
                AddingStrings.addCityAdmin(message.content.text)
            )
            sendTextMessage(
                UserId(state.snapshot.userId),
                AddingStrings.addCityUser(message.content.text)
            )
            state.override { DialogState.Empty }
        }
    }
    state<ChooseCityOrganizationAdminState> {
        selectCity(
            stringsCity = AddingStrings.CityOrganizationDropdown,
            onFinish = { _, state, cityId -> state.next(cityId) },
            onNone = { DialogState.Empty }
        )
    }
    state<ChooseOrganizationAdminState> {
        selectOrganization(
            stringsOrganization = AddingStrings.OrganizationDropdown,
            onFinish = { state, organizationId -> state.next(organizationId) },
            onNone = { state -> state.next(null) },
            cityId = { null }
        )
    }
    state<AddOrganizationAdminState> {
        onEnter { user ->
            if (state.snapshot.organizationId != null) {
                val city = getCityByIdUseCase(state.snapshot.cityId)!!
                val organization = getOrganizationByIdUseCase(state.snapshot.organizationId!!)!!
                addOrganizationCityUseCase(state.snapshot.cityId, state.snapshot.organizationId!!)
                sendTextMessage(
                    user,
                    AddingStrings.havingOrganizationAdmin(city.name, organization.name)
                )
                sendTextMessage(
                    UserId(state.snapshot.userId),
                    AddingStrings.havingOrganizationUser(city.name, organization.name)
                )
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(
                    user,
                    AddingStrings.InputOrganization,
                    replyMarkup = ReplyKeyboardRemove()
                )
            }
        }
        onText { message ->
            val organization = addOrganizationUseCase(message.content.text)
            val city = getCityByIdUseCase(state.snapshot.cityId)!!
            addOrganizationCityUseCase(city.id, organization.id)
            sendTextMessage(
                message.chat.id,
                AddingStrings.addOrganizationAdmin(city.name, organization.name)
            )
            sendTextMessage(
                UserId(state.snapshot.userId),
                AddingStrings.addOrganizationUser(city.name, organization.name)
            )
            state.override { DialogState.Empty }
        }
    }
}
