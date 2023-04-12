package addorganizations.telegram.flows

import addorganizations.domain.usecases.GetCityByIdUseCase
import addorganizations.domain.usecases.GetOrganizationByIdUseCase
import addorganizations.telegram.AddingStrings
import addorganizations.telegram.functions.sendConfirmAdding
import addorganizations.telegram.queries.AddCityQuery
import addorganizations.telegram.states.*
import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.functions.selectOrganization
import common.telegram.strings.DropdownWebAppStrings
import config.BotConfig
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import org.koin.core.component.inject

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
            sendTextMessage(message.chat.id, AddingStrings.sentCity(message.content.text))
            sendConfirmAdding(message.chat.id.chatId, botConfig.adminId!!, message.content.text, false, null)
            state.override { ChooseOrganizationInputState(message.content.text) }
        }
    }
    state<ChooseOrganizationInputState> {
        selectOrganization(
            stringsOrganization = DropdownWebAppStrings.organizationDropdown,
            cityId = { null },
            onFinish = { state, organization -> state.next(organization) },
            onNone = { state -> state.next(null) }
        )
    }
    state<AddOrganizationCityUserState> {
        onEnter { user ->
            if (state.snapshot.organizationId != null) {
                val organization = getOrganizationByIdUseCase(state.snapshot.organizationId!!)!!.name
                sendConfirmAdding(user.chatId, botConfig.adminId!!, state.snapshot.city, false, organization)
                sendTextMessage(
                    user,
                    AddingStrings.sentOrganization(
                        organization,
                        state.snapshot.city
                    ),
                    replyMarkup = ReplyKeyboardRemove()
                )
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(
                    user,
                    AddingStrings.InputUniversity,
                    replyMarkup = ReplyKeyboardRemove()
                )
            }
        }
        onText { message ->
            sendTextMessage(
                message.chat.id,
                AddingStrings.sentOrganization(
                    message.content.text,
                    state.snapshot.city
                )
            )
            sendConfirmAdding(
                message.chat.id.chatId,
                botConfig.adminId!!,
                state.snapshot.city,
                false,
                message.content.text
            )
            state.override { DialogState.Empty }
        }
    }
    state<AddOrganizationUserState> {
        onEnter { user ->
            sendTextMessage(
                user,
                AddingStrings.InputUniversity,
                replyMarkup = ReplyKeyboardRemove()
            )
        }
        onText { message ->
            val city = getCityByIdUseCase(state.snapshot.cityId)!!.name
            sendTextMessage(
                message.chat.id,
                AddingStrings.sentOrganization(
                    message.content.text,
                    city
                )
            )
            sendConfirmAdding(
                message.chat.id.chatId,
                botConfig.adminId!!,
                city,
                true,
                message.content.text
            )
            state.override { DialogState.Empty }
        }
    }
}
