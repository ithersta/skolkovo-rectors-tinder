import auth.GetUserUseCase
import auth.User
import com.ithersta.tgbotapi.commands.cancelCommand
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.persistence.SqliteStateRepository
import fillingAccountInfo.fillingAccountInfoFlow
import states.ChooseCityState
import states.DialogState

fun stateMachine(getUser: GetUserUseCase) = stateMachine<DialogState, _>(
    getUser = { getUser(it.chatId) },
    stateRepository = SqliteStateRepository.create(historyDepth = 1),
    initialState = DialogState.Empty,
    includeHelp = true
) {
    cancelCommand(initialState = DialogState.Empty)

    role<User.Unauthenticated> {
        anyState {
            onCommand("start", null) {
                state.override { ChooseCityState }
            }
        }
    }
    role<User.Normal> {
        fillingAccountInfoFlow()
    }
    role<User.Admin> { }
    fallback()
}


