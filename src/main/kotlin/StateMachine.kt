import auth.domain.entities.User
import auth.domain.usecases.GetUserUseCase
import com.ithersta.tgbotapi.commands.cancelCommand
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.persistence.SqliteStateRepository
import flows.fillingAccountInfoFlow
import flows.meetingFlow
import states.ChooseCountry
import states.DialogState
import states.MeetingState
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import common.telegram.Query
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import menus.adminMenu
import menus.normalMenu

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
                state.override { WaitingForContact } // /ну пока так
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
    }
    role<User.Admin> {
        with(adminMenu) { invoke() }
    }
    fallback()
}
