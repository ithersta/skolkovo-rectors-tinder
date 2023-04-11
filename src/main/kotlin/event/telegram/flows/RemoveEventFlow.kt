package event.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import menus.states.MenuState

fun StateMachineBuilder<DialogState, User, UserId>.removeEventFlow() {
    role<User.Normal> {
        state<MenuState.RemoveEventState>{
            onEnter {
                sendTextMessage(it, "TODO")
            }
            onText{
                state.override { DialogState.Empty }
            }
        }
    }
}
