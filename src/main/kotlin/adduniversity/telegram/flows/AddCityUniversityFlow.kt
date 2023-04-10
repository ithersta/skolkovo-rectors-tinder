package adduniversity.telegram.flows

import adduniversity.telegram.Strings
import adduniversity.telegram.queries.AddCityQuery
import adduniversity.telegram.queries.AddUniversityQuery
import adduniversity.telegram.states.AddCityInUniversityState
import adduniversity.telegram.states.AddCityState
import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import generated.onDataCallbackQuery

fun RoleFilterBuilder<DialogState, User, User.Admin, UserId>.addCityUniversityFlow() {
    //todo: city check use case
    //todo: add city use case
    //todo: add university use case
    anyState {
        onDataCallbackQuery(AddCityQuery::class) { (data, message) ->
            sendTextMessage(message.from.id, Strings.addCity)
            state.override { AddCityState() }
        }
        onDataCallbackQuery(AddUniversityQuery::class) { (data, message) ->
            sendTextMessage(message.from.id, Strings.addCity)
            state.override { AddCityInUniversityState() }
        }
    }
    state<AddCityState> {
        onText { message ->
            //sendTextMessage(message.chat.id, "")

        }
    }
}