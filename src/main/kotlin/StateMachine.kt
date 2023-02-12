import auth.domain.entities.User
import auth.domain.usecases.GetUserUseCase
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.persistence.SqliteStateRepository
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import mute.telegram.muteFlow

fun stateMachine(getUser: GetUserUseCase) = stateMachine<DialogState, _>(
    getUser = { getUser(it.chatId) },
    stateRepository = SqliteStateRepository.create(historyDepth = 1),
    initialState = DialogState.Empty,
    includeHelp = true
) {
    onException { _, throwable ->
        throwable.printStackTrace()
    }
    //cancelCommand(initialState = DialogState.Empty)
    role<User.Unauthenticated> { }
    role<User.Normal> {
        anyState {
            onCommand("mute", null) {
                sendTextMessage(it.chat, "start test Ivan's mute feature")
                state.override { MuteStates.StartMute }
            }
        }
    }
    role<User.Admin> { }
    muteFlow()
    fallback()
}
