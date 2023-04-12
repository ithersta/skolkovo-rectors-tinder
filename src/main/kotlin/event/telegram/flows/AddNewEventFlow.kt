package event.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.strings.CommonStrings
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import event.domain.entities.Event
import event.domain.usecases.AddEventUseCase
import event.domain.usecases.GetAllExceptAdminUseCase
import event.telegram.Strings
import event.telegram.states.*
import kotlinx.datetime.toKotlinInstant
import menus.states.MenuState
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun StateMachineBuilder<DialogState, User, UserId>.addEventFlow() {
    val addEventUseCase: AddEventUseCase by inject()
    val getAllExceptAdminUseCase: GetAllExceptAdminUseCase by inject()
    role<User.Admin> {
        state<MenuState.AddEventState> {
            onEnter { sendTextMessage(it, Strings.ScheduleEvent.InputName, replyMarkup = ReplyKeyboardRemove()) }
            onText { state.override { InputBeginDateTimeState(it.content.text) } }
        }
        state<InputBeginDateTimeState> {
            onEnter { sendTextMessage(it, Strings.ScheduleEvent.InputBeginDateTime) }
            onText {
                val beginDateTime = try {
                    LocalDateTime.parse(
                        it.content.text,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                            .withZone(ZoneId.of("Europe/Moscow"))
                    )
                        .toInstant(ZoneOffset.UTC).toKotlinInstant()
                } catch (e: DateTimeParseException) {
                    sendTextMessage(
                        it.chat,
                        Strings.ScheduleEvent.InvalidDataFormat + Strings.ScheduleEvent.InputBeginDateTime
                    )
                    return@onText
                }
                state.override { InputEndDateTimeState(state.snapshot.name, beginDateTime) }
            }
        }
        state<InputEndDateTimeState> {
            onEnter { sendTextMessage(it, Strings.ScheduleEvent.InputEndDateTime) }
            onText {
                val endDateTime = try {
                    LocalDateTime.parse(
                        it.content.text,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                            .withZone(ZoneId.of("Europe/Moscow"))
                    )
                        .toInstant(ZoneOffset.UTC).toKotlinInstant()
                } catch (e: DateTimeParseException) {
                    sendTextMessage(
                        it.chat,
                        Strings.ScheduleEvent.InvalidDataFormat + Strings.ScheduleEvent.InputEndDateTime
                    )
                    return@onText
                }
                if (endDateTime.epochSeconds < state.snapshot.beginDateTime.epochSeconds) {
                    sendTextMessage(
                        it.chat,
                        Strings.ScheduleEvent.InvalidTimeInterval
                    )
                    state.override { InputBeginDateTimeState(state.snapshot.name) }
                } else {
                    state.override {
                        InputDescriptionState(
                            state.snapshot.name,
                            state.snapshot.beginDateTime,
                            endDateTime
                        )
                    }
                }
            }
        }
        state<InputDescriptionState> {
            onEnter {
                sendTextMessage(
                    it,
                    Strings.ScheduleEvent.InputDescription,
                    replyMarkup = replyKeyboard(
                        resizeKeyboard = true,
                        oneTimeKeyboard = true
                    ) {
                        row {
                            simpleButton(Strings.ScheduleEvent.NoDescription)
                        }
                    }
                )
            }
            onText(Strings.ScheduleEvent.NoDescription) {
                state.override {
                    InputUrlState(
                        state.snapshot.name,
                        state.snapshot.beginDateTime,
                        state.snapshot.endDateTime
                    )
                }
            }
            onText {
                state.override {
                    InputUrlState(
                        state.snapshot.name,
                        state.snapshot.beginDateTime,
                        state.snapshot.endDateTime,
                        it.content.text
                    )
                }
            }
        }
        state<InputUrlState> {
            onEnter { sendTextMessage(it, Strings.ScheduleEvent.InputUrl) }
            onText {
                state.override {
                    AskUserToCreateEvent(
                        state.snapshot.name,
                        state.snapshot.beginDateTime,
                        state.snapshot.endDateTime,
                        state.snapshot.description,
                        it.content.text
                    )
                }
            }
        }
        state<AskUserToCreateEvent> {
            onEnter {
                val event = Event(
                    state.snapshot.name,
                    state.snapshot.beginDateTime,
                    state.snapshot.endDateTime,
                    state.snapshot.description,
                    state.snapshot.url
                )
                sendTextMessage(
                    it,
                    Strings.ScheduleEvent.message(event),
                    replyMarkup = replyKeyboard(
                        resizeKeyboard = true,
                        oneTimeKeyboard = true
                    ) {
                        row {
                            simpleButton(CommonStrings.Button.No)
                            simpleButton(CommonStrings.Button.Yes)
                        }
                    }
                )
            }
            onText(CommonStrings.Button.Yes) {
                val event = Event(
                    state.snapshot.name,
                    state.snapshot.beginDateTime,
                    state.snapshot.endDateTime,
                    state.snapshot.description,
                    state.snapshot.url
                )
                addEventUseCase(event)
                getAllExceptAdminUseCase(it.messageId).forEach { user ->
                    val id = user?.id?.toChatId()
                    if (id != null) {
                        runCatching {
                            sendTextMessage(id, Strings.eventMessage(event))
                        }
                    }
                }
                sendTextMessage(it.chat, Strings.ScheduleEvent.EventIsCreated)
                state.override { DialogState.Empty }
            }
            onText(CommonStrings.Button.No) {
                sendTextMessage(it.chat, Strings.ScheduleEvent.EventNotCreated)
                state.override { DialogState.Empty }
            }
        }
    }
}
