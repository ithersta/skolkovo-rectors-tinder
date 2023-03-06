package event.telegram

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import menus.states.MenuState

fun StateMachineBuilder<DialogState, User, UserId>.eventFlow() {
    role<User.Normal> {
        state<MenuState.Events> {
            onEnter {
                sendTextMessage(
                    it.toChatId(),
                    "Ссылка на мероприятия: https://www.skolkovo.ru/navigator/events/"
                )
            }
        }
    }
}
