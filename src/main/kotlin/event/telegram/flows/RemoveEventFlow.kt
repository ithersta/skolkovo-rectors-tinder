package event.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import event.domain.usecases.DeleteEventUseCase
import event.domain.usecases.GetEventsUseCase
import event.telegram.Strings
import menus.states.MenuState
import org.koin.core.component.inject

fun StateMachineBuilder<DialogState, User, UserId>.removeEventFlow() {
    val getEventsUseCase: GetEventsUseCase by inject()
    val deleteEventUseCase: DeleteEventUseCase by inject()
    role<User.Admin> {
        state<MenuState.RemoveEventState> {
            onEnter {
                sendTextMessage(it, Strings.RemoveEvent.ChooseEvent)
                // TODO пагинация
            }
            onText {
                state.override { DialogState.Empty }
            }
            // сначала будет список inline кнопок с названиями мероприятий(пагинация)
            // далее при нажатии на кнопку, выводится информация о мероприятии +
            // вы действительно хотите удалить мероприятие?
            // и 2 кнопки - да/нет
            // все это должно быть в anyState
        }
    }
}
