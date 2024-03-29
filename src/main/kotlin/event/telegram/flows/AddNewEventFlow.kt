package event.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.functions.fromMessage
import common.telegram.strings.CommonStrings
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.utils.row
import event.domain.entities.Event
import event.domain.usecases.AddEventUseCase
import event.telegram.Strings
import event.telegram.states.*
import kotlinx.datetime.*
import menus.states.MenuState
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun StateMachineBuilder<DialogState, User, UserId>.addEventFlow() {
    val addEventUseCase: AddEventUseCase by inject()
    val timeZone: TimeZone by inject()
    val midnight = LocalTime(0, 0)
    role<User.Admin> {
        state<MenuState.AddEventState> {
            onEnter {
                sendTextMessage(
                    it,
                    Strings.ScheduleEvent.InputName,
                    replyMarkup = ReplyKeyboardRemove()
                )
            }
            onText { Event.Name.fromMessage(it) { state.override { InputBeginDateTimeState(it) } } }
        }
        state<InputBeginDateTimeState> {
            onEnter {
                sendTextMessage(
                    it,
                    Strings.ScheduleEvent.InputBeginDateTime,
                    replyMarkup = ReplyKeyboardRemove()
                )
            }
            onText {
                val beginDateTime = try {
                    LocalDateTime.parse(
                        it.content.text,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                    ).toKotlinLocalDateTime().toInstant(timeZone)
                } catch (e: DateTimeParseException) {
                    val beginDate = try {
                        LocalDate.parse(
                            it.content.text,
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        ).toKotlinLocalDate().atStartOfDayIn(timeZone)
                    } catch (e: DateTimeParseException) {
                        sendTextMessage(
                            it.chat,
                            Strings.ScheduleEvent.InvalidDataFormat + Strings.ScheduleEvent.InputBeginDateTime
                        )
                        return@onText
                    }
                    state.override { InputEndDateTimeState(name, beginDate) }
                    return@onText
                }
                state.override { InputEndDateTimeState(name, beginDateTime) }
            }
        }
        state<InputEndDateTimeState> {
            onEnter {
                if (state.snapshot.beginDateTime.toLocalDateTime(timeZone).time == midnight) {
                    sendTextMessage(it, Strings.ScheduleEvent.InputEndDate)
                } else {
                    sendTextMessage(it, Strings.ScheduleEvent.InputEndDateTime)
                }
            }
            onText {
                if (state.snapshot.beginDateTime.toLocalDateTime(timeZone).time == midnight) {
                    val endDate = try {
                        LocalDate.parse(
                            it.content.text,
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        ).toKotlinLocalDate().atStartOfDayIn(timeZone)
                    } catch (e: DateTimeParseException) {
                        sendTextMessage(
                            it.chat,
                            Strings.ScheduleEvent.InvalidDataFormat + Strings.ScheduleEvent.InputEndDate
                        )
                        return@onText
                    }
                    if (endDate.epochSeconds <= state.snapshot.beginDateTime.epochSeconds) {
                        sendTextMessage(
                            it.chat,
                            Strings.ScheduleEvent.InvalidTimeInterval
                        )
                        state.override { InputBeginDateTimeState(state.snapshot.name) }
                    } else {
                        state.override {
                            InputDescriptionState(name, beginDateTime, endDate)
                        }
                    }
                } else {
                    val endDateTime = try {
                        LocalDateTime.parse(
                            it.content.text,
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                        ).toKotlinLocalDateTime().toInstant(timeZone)
                    } catch (e: DateTimeParseException) {
                        sendTextMessage(
                            it.chat,
                            Strings.ScheduleEvent.InvalidDataFormat + Strings.ScheduleEvent.InputEndDateTime
                        )
                        return@onText
                    }
                    if (endDateTime.epochSeconds <= state.snapshot.beginDateTime.epochSeconds) {
                        sendTextMessage(
                            it.chat,
                            Strings.ScheduleEvent.InvalidTimeInterval
                        )
                        state.override { InputBeginDateTimeState(state.snapshot.name) }
                    } else {
                        state.override {
                            InputDescriptionState(name, beginDateTime, endDateTime)
                        }
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
                state.override { InputUrlState(name, beginDateTime, endDateTime) }
            }
            onText { message ->
                Event.Description.fromMessage(message) {
                    state.override { InputUrlState(name, beginDateTime, endDateTime, it) }
                }
            }
        }
        state<InputUrlState> {
            onEnter { sendTextMessage(it, Strings.ScheduleEvent.InputUrl) }
            onText {
                Event.Url.of(it.content.text).onRight { url ->
                    state.override {
                        AskUserToCreateEvent(
                            name,
                            beginDateTime,
                            endDateTime,
                            description,
                            url
                        )
                    }
                }.onLeft { error ->
                    val text = when (error) {
                        Event.Url.Error.InvalidUrl -> Strings.ScheduleEvent.InvalidLink
                        is Event.Url.Error.MaxLengthExceeded -> CommonStrings.maxLengthExceeded(error.maxLength)
                    }
                    sendTextMessage(it.chat, text)
                    state.override { this }
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
                    Strings.ScheduleEvent.approveEventMessage(event, timeZone),
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

suspend fun TelegramBot.sendEventMessage(
    chatId: ChatId,
    event: Event,
    timeZone: TimeZone
) = sendTextMessage(
    chatId,
    Strings.newEventMessage(event, timeZone)
)
