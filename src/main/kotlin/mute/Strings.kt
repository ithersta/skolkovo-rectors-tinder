package mute

object Strings {
    const val muteBot = "Приостановить бота"
    const val muteWeek = "на 7 дней"
    const val muteMonth = "на 30 дней"
    const val yes = "да"
    const val no = "нет"
    const val unmuteQuestion = "Возобновить?"
    fun muteDays(days: Int) = "Уведомления бота отключены на $days дней"
}
