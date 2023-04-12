package addorganizations.telegram.flows
/*
import addorganizations.telegram.AddingStrings
import addorganizations.telegram.queries.AddCityQuery
import addorganizations.telegram.queries.AddOrganizationQuery
import addorganizations.telegram.states.AddCityInOrganizationState
import addorganizations.telegram.states.AddCityState
import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import generated.onDataCallbackQuery

fun RoleFilterBuilder<DialogState, User, User.Admin, UserId>.addCityOrganizationAdminFlow() {
    //todo: city check use case
    //todo: add city use case
    //todo: add university use case
    anyState {
        onDataCallbackQuery(AddCityQuery::class) { (data, message) ->
            sendTextMessage(message.from.id, AddingStrings.InputCity)
            state.override { AddCityState() }
        }
        onDataCallbackQuery(AddOrganizationQuery::class) { (data, message) ->
            sendTextMessage(message.from.id, AddingStrings.InputCity)
            state.override { AddCityInOrganizationState() }
        }
    }
    state<AddCityState> {
        onText { message ->
            //sendTextMessage(message.chat.id, "")

        }
    }
}*/
