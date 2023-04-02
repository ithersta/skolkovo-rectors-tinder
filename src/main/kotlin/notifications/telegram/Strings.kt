package notifications.telegram

import dev.inmo.tgbotapi.types.message.textsources.TextSourcesList
import dev.inmo.tgbotapi.utils.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalTime
import notifications.domain.entities.NotificationPreference
import notifications.domain.usecases.QuestionNotificationConfig
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*

object Strings {
    fun NotificationPreference.localizedString() = when (this) {
        NotificationPreference.RightAway -> "Сразу"
        NotificationPreference.Daily -> "Ежедневно"
        NotificationPreference.Weekly -> "Еженедельно"
    }

    fun newQuestionsMessage(notificationPreference: NotificationPreference) = when (notificationPreference) {
        NotificationPreference.RightAway -> error("Impossible")
        NotificationPreference.Daily -> "Новые вопросы за день"
        NotificationPreference.Weekly -> "Новые вопросы за неделю"
    }

    object Settings {
        fun message(
            questionNotificationConfig: QuestionNotificationConfig,
            timeZone: TimeZone
        ): TextSourcesList {
            require(timeZone.id == "Europe/Moscow") { "TimeZone must be Europe/Moscow as it's mentioned in the text" }
            val locale = Locale.forLanguageTag("ru")
            val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
            val time = questionNotificationConfig.notifyAt.toJavaLocalTime()
            val dayOfWeekDisplayName = questionNotificationConfig.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
            return buildEntities {
                regularln("Выберите, как часто Вы хотите получать уведомления о новых вопросах.")
                regular("Ежедневная рассылка осуществляется в ")
                bold(timeFormatter.format(time))
                regular(" по московскому времени. Еженедельная рассылка осуществляется каждый ")
                bold(dayOfWeekDisplayName)
                regularln(".")
            }
        }

        const val TurnOn = "Включить"
        const val TurnOff = "Выключить"
    }
}
