import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.flows.fillingAccountInfoFlow
import com.ithersta.tgbotapi.boot.annotations.StateMachine
import com.ithersta.tgbotapi.commands.cancelCommand
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import common.telegram.Query
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import menus.normalMenu
import states.WriteNameState

@StateMachine(baseQueryKClass = Query::class)
val stateMachine = stateMachine<DialogState, User, UserId>(
    initialState = DialogState.Empty,
    includeHelp = true
) {
    cancelCommand(initialState = DialogState.Empty)

    role<User.Unauthenticated> {
        with(normalMenu) { invoke() } // /TODO: потом убрать

        fillingAccountInfoFlow()
        anyState {
            onCommand("start", null) {
                // //сначала проверить номер на наличие в базе данных и отсутствие данных об аккаунте
                state.override { WriteNameState(PhoneNumber.of("79290367450")!!) } // /ну пока так
            }
        }
        state<DialogState.Empty> {
            onEnter {
                sendTextMessage(
                    it,
                    Strings.RoleMenu.Unauthenticated
                )
            }
        }
    }
    role<User.Normal> {
        // /with(normalMenu) { invoke() }
        state<DialogState.Empty> {
            onEnter {
                sendTextMessage(
                    it,
                    Strings.RoleMenu.Normal
                )
            }
        }
    }
    role<User.Admin> {
        state<DialogState.Empty> {
            onEnter {
                sendTextMessage(
                    it,
                    Strings.RoleMenu.Admin
                )
            }
        }
    }
    fallback()
}
