import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.usecases.GetUserUseCase
import auth.telegram.flows.fillingAccountInfoFlow
import com.ithersta.tgbotapi.commands.cancelCommand
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.persistence.SqliteStateRepository
import states.DialogState
import states.WriteNameState

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
                state.override { WriteNameState(PhoneNumber.of("79290367450")!!) } // /ну пока так
            }
        }
    }
    role<User.Normal> {
    }
    role<User.Admin> { }
    fallback()
}
