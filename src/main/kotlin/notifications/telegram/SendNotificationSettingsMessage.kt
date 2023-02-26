package notifications.telegram

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.StatefulContext
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.message.content.TextMessage
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import mute.domain.usecases.ContainsByIdMuteSettingsUseCase
import mute.telegram.queries.OnOffMuteQuery
import notifications.domain.entities.NotificationPreference
import notifications.domain.usecases.GetNotificationPreferenceUseCase
import notifications.telegram.Strings.localizedString
import notifications.telegram.queries.ChangeNotificationPreferenceQuery
import org.koin.core.context.GlobalContext

private val containsByIdMuteSettings: ContainsByIdMuteSettingsUseCase by GlobalContext.get().inject()
private val getNotificationPreference: GetNotificationPreferenceUseCase by GlobalContext.get().inject()

suspend fun <S : User> StatefulContext<DialogState, User, *, S>.sendNotificationPreferencesMessage(
    message: TextMessage
) = sendTextMessage(
    message.chat,
    Strings.Main.Message,
    replyMarkup = notificationPreferencesInlineKeyboard(message.chat.id.chatId)
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
            dataButton(Strings.Main.TurnOn, OnOffMuteQuery(true))
        } else {
            dataButton(Strings.Main.TurnOff, OnOffMuteQuery(false))
        }
    }
}
