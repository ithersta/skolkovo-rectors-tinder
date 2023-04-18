package event.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.BaseStatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import com.ithersta.tgbotapi.pagination.replyMarkup
import common.telegram.DialogState
import common.telegram.functions.confirmationInlineKeyboard
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContentOrThrow
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import event.domain.usecases.DeleteEventUseCase
import event.domain.usecases.GetEventByIdUseCase
import event.domain.usecases.GetEventsPaginatedUseCase
import event.telegram.Strings
import event.telegram.queries.DeleteEvent
import event.telegram.queries.NotDeleteEvent
import event.telegram.queries.SelectEvent
import generated.dataButton
import generated.onDataCallbackQuery
import org.koin.core.component.inject

private lateinit var eventPager: InlineKeyboardPager<Unit, DialogState, User, User.Admin>
suspend fun BaseStatefulContext<DialogState, User, *, out User.Admin>.sendListOfEvents(
    chatIdentifier: IdChatIdentifier
) {
    val replyMarkup = eventPager.replyMarkup
    if (replyMarkup.keyboard.isEmpty()) {
        sendTextMessage(chatIdentifier, Strings.NoEvent)
        state.override { DialogState.Empty }
    } else {
        sendTextMessage(chatIdentifier, Strings.RemoveEvent.ChooseEvent, replyMarkup = replyMarkup)
    }
}

fun RoleFilterBuilder<DialogState, User, User.Admin, UserId>.removeEventFlow() {
    val getEventsPaginatedUseCase: GetEventsPaginatedUseCase by inject()
    val getEventByIdUseCase: GetEventByIdUseCase by inject()
    val deleteEventUseCase: DeleteEventUseCase by inject()

    eventPager = pager(id = "events") {
        val event = getEventsPaginatedUseCase(offset, limit)
        inlineKeyboard {
            event.slice.forEach { item ->
                row {
                    item.id?.let { SelectEvent(it) }?.let { dataButton(item.name, it) }
                }
            }
            navigationRow(itemCount = event.count)
        }
    }
    anyState {
        onDataCallbackQuery(SelectEvent::class) { (data, query) ->
            val event = getEventByIdUseCase(data.id)
            sendTextMessage(
                query.user.id,
                Strings.RemoveEvent.removeEventMessage(event),
                replyMarkup = confirmationInlineKeyboard(
                    positiveData = DeleteEvent(data.id),
                    negativeData = NotDeleteEvent(data.id)
                )
            )
            answer(query)
        }
        onDataCallbackQuery(DeleteEvent::class) { (data, query) ->
            deleteEventUseCase(data.id)
            val event = getEventByIdUseCase(data.id)
            // пока не работает java.util.NoSuchElementException: List is empty.
            edit(
                query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>(),
                Strings.RemoveEvent.removedEventMessage(event),
                replyMarkup = null
            )
            answer(query)
        }
        onDataCallbackQuery(NotDeleteEvent::class) { (data, query) ->
            val event = getEventByIdUseCase(data.id)
            // а тут работает
            edit(
                query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>(),
                Strings.RemoveEvent.notRemovedEventMessage(event),
                replyMarkup = null
            )
            answer(query)
        }
    }
}
