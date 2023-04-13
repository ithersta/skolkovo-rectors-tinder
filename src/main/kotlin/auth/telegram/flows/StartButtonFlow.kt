package auth.telegram.flows

import auth.domain.entities.User
import auth.telegram.queries.StartQuery
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.UserId
import generated.onDataCallbackQuery

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.startButtonFlow() {
    anyState {
        onDataCallbackQuery(StartQuery::class) {
            state.override { DialogState.Empty }
        }
    }
}
