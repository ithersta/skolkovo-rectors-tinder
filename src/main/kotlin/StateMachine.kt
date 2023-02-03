import auth.GetUserUseCase
import auth.User
import com.ithersta.tgbotapi.commands.cancelCommand
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.persistence.SqliteStateRepository

fun stateMachine(getUser: GetUserUseCase) = stateMachine(
    getUser = { getUser(it.chatId) },
    stateRepository = SqliteStateRepository.create(historyDepth = 1),
    initialState = DialogState.Empty,
    includeHelp = true
) {
    cancelCommand(initialState = DialogState.Empty)
    role<User.Unauthenticated> { }
    role<User.Normal> { }
    role<User.Admin> { }
    fallback()
}
