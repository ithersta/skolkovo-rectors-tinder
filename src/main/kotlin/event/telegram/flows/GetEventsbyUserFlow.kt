package event.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import event.domain.usecases.GetEventsUseCase
import event.telegram.Strings
import kotlinx.datetime.TimeZone
import menus.states.MenuState
import org.koin.core.component.inject

fun StateMachineBuilder<DialogState, User, UserId>.getEventsByUserFlow() {
    val getEventsUseCase: GetEventsUseCase by inject()
    val timeZone: TimeZone by inject()
    role<User.Normal> {
        state<MenuState.Events> {
            onEnter {
                val events = getEventsUseCase()
                if (events.isEmpty()) {
                    sendTextMessage(it, Strings.NoEvent)
                } else {
                    val entities = events
                        .sortedBy { event -> event.timestampBegin }
                        .flatMap { event -> Strings.eventMessage(event, timeZone) }
                    sendTextMessage(it, entities)
                }
                state.override { DialogState.Empty }
            }
        }
    }
}
