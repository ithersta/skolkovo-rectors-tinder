package notifications.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import kotlinx.datetime.TimeZone
import mute.domain.usecases.ContainsByIdMuteSettingsUseCase
import mute.telegram.queries.OnOffMuteQuery
import notifications.domain.entities.NotificationPreference
import notifications.domain.usecases.GetNotificationPreferenceUseCase
import notifications.domain.usecases.QuestionNotificationConfig
import notifications.telegram.Strings.localizedString
import notifications.telegram.queries.ChangeNotificationPreferenceQuery
import org.koin.core.context.GlobalContext

private val containsByIdMuteSettings: ContainsByIdMuteSettingsUseCase by GlobalContext.get().inject()
private val getNotificationPreference: GetNotificationPreferenceUseCase by GlobalContext.get().inject()
private val timeZone: TimeZone by GlobalContext.get().inject()
private val questionNotificationConfig: QuestionNotificationConfig by GlobalContext.get().inject()

suspend fun TelegramBot.sendNotificationPreferencesMessage(
    idChatIdentifier: IdChatIdentifier
) = sendTextMessage(
    idChatIdentifier,
    Strings.Settings.message(questionNotificationConfig, timeZone),
    replyMarkup = notificationPreferencesInlineKeyboard(idChatIdentifier.chatId)
)

fun notificationPreferencesInlineKeyboard(userId: Long) = inlineKeyboard {
    val notificationPreference = getNotificationPreference(userId)
    row {
        NotificationPreference.values().forEach {
            dataButton(
                text = buildString {
                    if (it == notificationPreference) append('✅')
                    append(it.localizedString())
                },
                data = ChangeNotificationPreferenceQuery(it)
            )
        }
    }
    row {
        if (containsByIdMuteSettings(userId)) {
            dataButton(Strings.Settings.TurnOn, OnOffMuteQuery(true))
        } else {
            dataButton(Strings.Settings.TurnOff, OnOffMuteQuery(false))
        }
    }
}
