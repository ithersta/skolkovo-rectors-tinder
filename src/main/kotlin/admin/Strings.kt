package admin

import auth.domain.entities.PhoneNumber

object Strings {
    const val WaitDocument = "Заполните шаблон и прикрепите ответным сообщением"
    const val TemplateFileName = "Шаблон"
    const val InvalidFile = "Файл повреждён или не является .xlsx таблицей"
    const val addingUsers = "все пользователи добавлены"
    fun blockedUsers(blocked: List<PhoneNumber>) = "Вы пытались добавить заблокированных пользователей: " +
        blocked.joinToString(separator = ", ") { it.value }

    fun badFormat(errors: List<Int>) =
        "неправильный формат в ${errors.size} ${pluralize(errors.size, "строчке", "строчках", "строчках")}: " +
            errors.joinToString(separator = ", ") { it.toString() }

    @Suppress("MagicNumber")
    fun pluralize(count: Int, one: String, few: String, many: String) = when {
        count % 10 == 1 && count % 100 != 11 -> one
        count % 10 in 2..4 && count % 100 !in 12..14 -> few
        else -> many
    }
}
