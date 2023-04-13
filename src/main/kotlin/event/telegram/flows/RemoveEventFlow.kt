package event.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import event.domain.usecases.DeleteEventUseCase
import event.domain.usecases.GetEventsPaginatedUseCase
import event.telegram.Strings
import event.telegram.queries.SelectEvent
import generated.dataButton
import menus.states.MenuState
import org.koin.core.component.inject

private lateinit var eventPager: InlineKeyboardPager<Unit, DialogState, User, User.Admin>
fun RoleFilterBuilder<DialogState, User, User.Admin, UserId>.removeEventFlow() {
    val getEventsPaginatedUseCase: GetEventsPaginatedUseCase by inject()
    val deleteEventUseCase: DeleteEventUseCase by inject()

    eventPager = pager(id = "events") {
        val event = getEventsPaginatedUseCase(offset, limit)
        inlineKeyboard {
            event.slice.forEach { item ->
                row {
                    dataButton(item.name, SelectEvent(1))//TODO
                }
            }
            navigationRow(itemCount = event.count)
        }
    }
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
