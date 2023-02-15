package mute.telegram

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onDataCallbackQuery
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.extensions.utils.withContent
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import mute.Strings
import mute.data.usecases.InsertMuteSettingsUseCase
import mute.telegram.states.MuteStates
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.days

fun StateMachineBuilder<DialogState, User, UserId>.muteFlow() {
    val insertMuteSettingsUseCase: InsertMuteSettingsUseCase by inject()
    anyRole {
        state<MuteStates.StartMute> {
            onEnter { user ->
                sendTextMessage(
                    user.toChatId(),
                    Strings.muteBot,
                    parseMode = MarkdownV2,
                    replyMarkup = replyKeyboard {
                        row {
                            simpleButton(Strings.muteWeek)
                            simpleButton(Strings.muteMonth)
                        }
                    }
                )
            }
            onText(Strings.muteWeek) { user ->
                sendTextMessage(
                    user.chat,
                    Strings.muteDays(7),
                    parseMode = MarkdownV2,
                    replyMarkup = ReplyKeyboardRemove()
                )
                insertMuteSettingsUseCase(user.chat.id.chatId, 7.days)
                state.override { MuteStates.MutePause }
            }
            onText(Strings.muteMonth) { user ->
                sendTextMessage(
                    user.chat,
                    Strings.muteDays(30),
                    parseMode = MarkdownV2,
                    replyMarkup = ReplyKeyboardRemove()
                )
                insertMuteSettingsUseCase(user.chat.id.chatId, 30.days)
                state.override { MuteStates.MutePause }
            }
        }
        state<MuteStates.MutePause> {
            onDataCallbackQuery(Regex("yes")) { message ->
                editMessageReplyMarkup(
                    message.asMessageCallbackQuery()?.message?.withContent<TextContent>() ?: return@onDataCallbackQuery,
                    replyMarkup = null
                )
                // todo: override state to start_state???
            }
            onDataCallbackQuery(Regex("no")) { message ->
                editMessageReplyMarkup(
                    message.asMessageCallbackQuery()?.message?.withContent<TextContent>() ?: return@onDataCallbackQuery,
                    replyMarkup = null
                )
                state.override { MuteStates.StartMute }
            }
        }
    }
}
