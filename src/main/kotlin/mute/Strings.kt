package mute

object Strings {
    const val MuteOff = "Уведомления включены"
    const val MuteBot = "Отключить уведомления бота"
    const val MuteWeek = "На 7 дней"
    const val MuteMonth = "На 30 дней"
    const val Yes = "Да"
    const val No = "Нет"
    const val UnmuteQuestion = "Продлить время отключения уведомлений?"
    fun muteDays(days: Long) = "Уведомления бота отключены на $days дней"
}
