package mute.telegram

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContent
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import menus.states.MenuState
import mute.Strings
import mute.domain.usecases.ContainsByIdMuteSettingsUseCase
import mute.domain.usecases.DeleteMuteSettingsUseCase
import mute.domain.usecases.InsertMuteSettingsUseCase
import mute.telegram.queries.OnOffMuteQuery
import mute.telegram.queries.WeekMonthMuteQuery
import mute.telegram.queries.YesNoMuteQuery
import mute.telegram.states.MuteStates
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.days

fun StateMachineBuilder<DialogState, User, UserId>.muteFlow() {
    val insertMuteSettingsUseCase: InsertMuteSettingsUseCase by inject()
    val containsByIdMuteSettingsUseCase: ContainsByIdMuteSettingsUseCase by inject()
    val deleteMuteSettingsUseCase: DeleteMuteSettingsUseCase by inject()
    role<User.Normal> {
        state<MenuState.Notifications> {
            onEnter { user ->
                sendTextMessage(
                    user.toChatId(),
                    auth.telegram.Strings.MenuButtons.Notifications.Description,
                    replyMarkup = inlineKeyboard {
                        row {
                            if (containsByIdMuteSettingsUseCase(user.chatId)) {
                                dataButton(auth.telegram.Strings.MenuButtons.Notifications.On, OnOffMuteQuery(true))
                            } else {
                                dataButton(auth.telegram.Strings.MenuButtons.Notifications.Off, OnOffMuteQuery(false))
                            }
                        }
                    }
                )
            }
            onDataCallbackQuery(OnOffMuteQuery::class) { (data, message) ->
                editMessageReplyMarkup(
                    message.asMessageCallbackQuery()?.message?.withContent<TextContent>() ?: return@onDataCallbackQuery,
                    replyMarkup = null
                )
                if (data.on) {
                    deleteMuteSettingsUseCase(message.user.id.chatId)
                    sendTextMessage(
                        message.user,
                        Strings.muteOff
                    )
                    state.override { DialogState.Empty }
                } else {
                    state.override { MuteStates.StartMute }
                }
            }
        }
        state<MuteStates.StartMute> {
            onEnter { user ->
                sendTextMessage(
                    user.toChatId(),
                    Strings.muteBot,
                    replyMarkup = inlineKeyboard {
                        row {
                            dataButton(Strings.muteWeek, WeekMonthMuteQuery(true))
                            dataButton(Strings.muteMonth, WeekMonthMuteQuery(false))
                        }
                    }
                )
            }
            onDataCallbackQuery(WeekMonthMuteQuery::class) { (data, message) ->
                val day = if (data.week) 7 else 30
                sendTextMessage(
                    message.user,
                    Strings.muteDays(7),
                )
                insertMuteSettingsUseCase(message.user.id.chatId, day.days)
                state.override { DialogState.Empty }
            }
        }
        anyState {
            onDataCallbackQuery(YesNoMuteQuery::class) { (data, message) ->
                editMessageReplyMarkup(
                    message.asMessageCallbackQuery()?.message?.withContent<TextContent>() ?: return@onDataCallbackQuery,
                    replyMarkup = null
                )
                if (data.yes) {
                    state.override { DialogState.Empty }
                } else {
                    state.override { MuteStates.StartMute }
                }
            }
        }
    }
}
