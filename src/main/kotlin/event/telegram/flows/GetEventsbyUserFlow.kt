package event.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.buildEntities
import event.domain.usecases.GetEventsUseCase
import event.telegram.Strings
import menus.states.MenuState
import org.koin.core.component.inject

fun StateMachineBuilder<DialogState, User, UserId>.getEventsByUserFlow() {
    val getEventsUseCase: GetEventsUseCase by inject()
    role<User.Normal> {
        state<MenuState.Events> {
            //TODO переделать, так как выводится не то
            onEnter {
                val entities = buildEntities {
                    getEventsUseCase.invoke().forEach() { event ->
                        addAll(Strings.eventMessage(event))
                    }
                }
                sendTextMessage(it, entities)
                state.override { DialogState.Empty }
            }
        }
    }
}
