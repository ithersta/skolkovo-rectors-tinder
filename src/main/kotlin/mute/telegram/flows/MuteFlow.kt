package mute.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.utils.asMessageCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContent
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import mute.domain.usecases.ContainsByIdMuteSettingsUseCase
import mute.domain.usecases.DeleteMuteSettingsUseCase
import mute.domain.usecases.InsertMuteSettingsUseCase
import mute.telegram.Strings
import mute.telegram.queries.DurationMuteQuery
import mute.telegram.queries.OnOffMuteQuery
import mute.telegram.queries.YesNoMuteQuery
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.days

@OptIn(PreviewFeature::class)
fun StateMachineBuilder<DialogState, User, UserId>.muteFlow() {
    val insertMuteSettingsUseCase: InsertMuteSettingsUseCase by inject()
    val deleteMuteSettingsUseCase: DeleteMuteSettingsUseCase by inject()
    val containsByIdMuteSettingsUseCase: ContainsByIdMuteSettingsUseCase by inject()
    role<User.Normal> {
        anyState {
            onDataCallbackQuery(OnOffMuteQuery::class) { (data, message) ->
                editMessageReplyMarkup(
                    message.asMessageCallbackQuery()?.message?.withContent<TextContent>() ?: return@onDataCallbackQuery,
                    replyMarkup = null
                )
                if (data.on) {
                    deleteMuteSettingsUseCase(message.user.id.chatId)
                    editMessageText(
                        message.asMessageCallbackQuery()?.message?.withContent<TextContent>()
                            ?: return@onDataCallbackQuery,
                        Strings.MuteOff,
                        replyMarkup = null
                    )
                    state.override { DialogState.Empty }
                } else {
                    editMessageText(
                        message.asMessageCallbackQuery()?.message?.withContent<TextContent>()
                            ?: return@onDataCallbackQuery,
                        Strings.MuteBot,
                        replyMarkup = inlineKeyboard {
                            row {
                                dataButton(Strings.MuteWeek, DurationMuteQuery(7.days))
                                dataButton(Strings.MuteMonth, DurationMuteQuery(30.days))
                            }
                        }
                    )
                }
            }
            onDataCallbackQuery(DurationMuteQuery::class) { (data, message) ->
                editMessageText(
                    message.asMessageCallbackQuery()?.message?.withContent<TextContent>()
                        ?: return@onDataCallbackQuery,
                    Strings.muteDays(data.duration.inWholeDays),
                    replyMarkup = null
                )
                insertMuteSettingsUseCase(message.user.id.chatId, data.duration)
                state.override { DialogState.Empty }
            }
            onDataCallbackQuery(YesNoMuteQuery::class) { (data, message) ->
                editMessageReplyMarkup(
                    message.asMessageCallbackQuery()?.message?.withContent<TextContent>() ?: return@onDataCallbackQuery,
                    replyMarkup = null
                )
                if (data.yes) {
                    editMessageText(
                        message.asMessageCallbackQuery()?.message?.withContent<TextContent>()
                            ?: return@onDataCallbackQuery,
                        Strings.MuteBot,
                        replyMarkup = inlineKeyboard {
                            row {
                                if (containsByIdMuteSettingsUseCase(message.user.id.chatId)) {
                                    dataButton(notifications.telegram.Strings.Main.TurnOn, OnOffMuteQuery(true))
                                } else {
                                    dataButton(
                                        notifications.telegram.Strings.Main.TurnOff,
                                        OnOffMuteQuery(false)
                                    )
                                }
                            }
                        }
                    )
                } else {
                    editMessageReplyMarkup(
                        message.asMessageCallbackQuery()?.message?.withContent<TextContent>()
                            ?: return@onDataCallbackQuery,
                        replyMarkup = null
                    )
                    state.override { DialogState.Empty }
                }
            }
        }
    }
}
