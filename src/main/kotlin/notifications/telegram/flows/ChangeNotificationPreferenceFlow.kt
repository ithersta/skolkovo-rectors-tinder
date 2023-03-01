package notifications.telegram.flows

import auth.domain.entities.User
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import generated.RoleFilterBuilder
import generated.onDataCallbackQuery
import notifications.domain.usecases.UpdateNotificationPreferenceUseCase
import notifications.telegram.notificationPreferencesInlineKeyboard
import notifications.telegram.queries.ChangeNotificationPreferenceQuery
import org.koin.core.component.inject

fun RoleFilterBuilder<User.Normal>.changeNotificationPreferenceFlow() {
    val updateNotificationPreference: UpdateNotificationPreferenceUseCase by inject()
    anyState {
        onDataCallbackQuery(ChangeNotificationPreferenceQuery::class) { (data, query) ->
            updateNotificationPreference(query.from.id.chatId, data.newPreference)
            val message = query.messageCallbackQueryOrThrow().message
            editMessageReplyMarkup(message, notificationPreferencesInlineKeyboard(query.from.id.chatId))
            answer(query)
        }
    }
}
