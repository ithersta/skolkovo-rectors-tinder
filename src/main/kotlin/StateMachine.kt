import auth.domain.entities.User
import auth.domain.usecases.GetUserUseCase
import auth.telegram.Strings
import auth.telegram.flows.fillingAccountInfoFlow
import com.ithersta.tgbotapi.commands.cancelCommand
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.persistence.SqliteStateRepository
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import menus.adminMenu
import menus.normalMenu
import states.DialogState
import states.WaitingForContact

fun stateMachine(getUser: GetUserUseCase) = stateMachine<DialogState, _>(
    getUser = { getUser(it.chatId) },
    stateRepository = SqliteStateRepository.create(historyDepth = 1),
    initialState = DialogState.Empty,
    includeHelp = true
) {
    onException { _, throwable ->
        throwable.printStackTrace()
    }
    cancelCommand(initialState = DialogState.Empty)

    role<User.Unauthenticated> {
        fillingAccountInfoFlow()
        anyState {
            onCommand("start", null) {
                // //сначала проверить номер на наличие в базе данных и отсутствие данных об аккаунте
                state.override { WaitingForContact} // /ну пока так
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
        with(normalMenu) { invoke() }
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
        with(adminMenu) { invoke() }
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
