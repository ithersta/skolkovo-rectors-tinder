package mute

object Strings {
    const val muteOff = "Уведомления включены"
    const val muteBot = "Приостановить бота"
    const val muteWeek = "На 7 дней"
    const val muteMonth = "На 30 дней"
    const val yes = "Да"
    const val no = "Нет"
    const val unmuteQuestion = "Включить оповещения?"
    fun muteDays(days: Long) = "Уведомления бота отключены на $days дней"
}
