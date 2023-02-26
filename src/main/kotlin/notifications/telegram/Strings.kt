package notifications.telegram

import notifications.domain.entities.NotificationPreference

object Strings {
    fun NotificationPreference.localizedString() = when (this) {
        NotificationPreference.RightAway -> "Сразу"
        NotificationPreference.Daily -> "Ежедневно"
        NotificationPreference.Weekly -> "Еженедельно"
    }

    object Main {
        const val Message = "Выберите, как часто Вы хотите получать уведомления"
        const val TurnOn = "Включить"
        const val TurnOff = "Выключить"
    }
}
