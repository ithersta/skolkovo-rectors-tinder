package addorganizations.telegram.flows

import addorganizations.domain.usecases.GetCityByIdUseCase
import addorganizations.domain.usecases.GetOrganizationByIdUseCase
import addorganizations.telegram.AddingStrings
import addorganizations.telegram.functions.sendConfirmAdding
import addorganizations.telegram.states.*
import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.functions.fromMessage
import common.telegram.functions.selectOrganization
import common.telegram.strings.DropdownWebAppStrings
import config.BotConfig
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import org.koin.core.component.inject
import organizations.domain.entities.City
import organizations.domain.entities.Organization

fun <Role : User> RoleFilterBuilder<DialogState, User, Role, UserId>.addCityOrganizationUserFlow() {
    val botConfig: BotConfig by inject()
    val getOrganizationByIdUseCase: GetOrganizationByIdUseCase by inject()
    val getCityByIdUseCase: GetCityByIdUseCase by inject()
    state<AddCityUserState> {
        onEnter { user ->
            sendTextMessage(
                user,
                AddingStrings.InputCity,
                replyMarkup = ReplyKeyboardRemove()
            )
        }
        onText { message ->
            City.Name.fromMessage(message) { name ->
                sendTextMessage(message.chat.id, AddingStrings.sentCity(name))
                sendConfirmAdding(message.chat.id.chatId, botConfig.adminId, name, false, null)
                state.override { ChooseOrganizationInputState(name) }
            }
        }
    }
    state<ChooseOrganizationInputState> {
        selectOrganization(
            stringsOrganization = DropdownWebAppStrings.OrganizationDropdown,
            cityId = { null },
            onFinish = { state, organization -> state.next(organization) },
            onNone = { state -> state.next(null) }
        )
    }
    state<AddOrganizationCityUserState> {
        onEnter { user ->
            if (state.snapshot.organizationId != null) {
                val organization = getOrganizationByIdUseCase(state.snapshot.organizationId!!)!!.name
                sendConfirmAdding(user.chatId, botConfig.adminId, state.snapshot.cityName, false, organization)
                sendTextMessage(
                    user,
                    AddingStrings.sentOrganization(
                        organization,
                        state.snapshot.cityName
                    ),
                    replyMarkup = ReplyKeyboardRemove()
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
            Organization.Name.fromMessage(message) { organizationName ->
                sendTextMessage(
                    message.chat.id,
                    AddingStrings.sentOrganization(
                        organizationName,
                        state.snapshot.cityName
                    )
                )
                sendConfirmAdding(
                    message.chat.id.chatId,
                    botConfig.adminId,
                    state.snapshot.cityName,
                    false,
                    organizationName
                )
                state.override { DialogState.Empty }
            }
        }
    }
    state<AddOrganizationUserState> {
        onEnter { user ->
            sendTextMessage(
                user,
                AddingStrings.InputOrganization,
                replyMarkup = ReplyKeyboardRemove()
            )
        }
        onText { message ->
            Organization.Name.fromMessage(message) { organizationName ->
                val city = getCityByIdUseCase(state.snapshot.cityId)!!.name
                sendTextMessage(
                    message.chat.id,
                    AddingStrings.sentOrganization(organizationName, city)
                )
                sendConfirmAdding(
                    message.chat.id.chatId,
                    botConfig.adminId,
                    city,
                    true,
                    organizationName
                )
                state.override { DialogState.Empty }
            }
        }
    }
}
