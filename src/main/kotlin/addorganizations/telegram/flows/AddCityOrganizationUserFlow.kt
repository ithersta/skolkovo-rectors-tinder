package addorganizations.telegram.flows

import addorganizations.domain.usecases.GetCityByIdUseCase
import addorganizations.domain.usecases.GetOrganizationByIdUseCase
import addorganizations.telegram.AddingStrings
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
import dev.inmo.tgbotapi.types.UserId
import org.koin.core.component.inject

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.addCityOrganizationUserFlow() {
    val botConfig: BotConfig by inject()
    val getOrganizationByIdUseCase: GetOrganizationByIdUseCase by inject()
    val getCityByIdUseCase: GetCityByIdUseCase by inject()
    state<AddCityUserState> {
        onEnter { user ->
            sendTextMessage(user, AddingStrings.InputCity)
        }
        onText { message ->
            sendTextMessage(message.chat.id, AddingStrings.sentCity(message.content.text))
            state.override { ChooseOrganizationInputState(message.content.text) }
            //todo: отправить админу с userid
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
                sendTextMessage(
                    user,
                    AddingStrings.sentUniversity(
                        state.snapshot.city,
                        getOrganizationByIdUseCase(state.snapshot.organizationId!!)!!.name
                    )
                )
                //todo: отправить админу с userid
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(user, AddingStrings.InputUniversity)
            }
        }
        onText { message ->
            sendTextMessage(
                message.chat.id,
                AddingStrings.sentUniversity(
                    state.snapshot.city,
                    message.content.text
                )
            )
            //todo: отправить админу с userid
            state.override { DialogState.Empty }
        }
    }
    state<AddOrganizationUserState> {
        onEnter { user ->
            sendTextMessage(user, AddingStrings.InputUniversity)
        }
        onText { message ->
            sendTextMessage(
                message.chat.id,
                AddingStrings.sentUniversity(
                    getCityByIdUseCase(state.snapshot.cityId)!!.name,
                    message.content.text
                )
            )
            //todo: отправить админу с userid
            state.override { DialogState.Empty }
        }
    }
}
