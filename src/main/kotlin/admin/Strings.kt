package admin

import auth.domain.entities.PhoneNumber
import dev.inmo.tgbotapi.types.queryField

object Strings {
    const val WaitDocument = "«аполните шаблон и прикрепите ответным сообщением"
    const val TemplateFileName = "Ўаблон"
    const val InvalidFile = "‘айл поврежден или не €вл€етс€ .xlsx таблицей"
    const val addingUsers = "¬се пользователеи были добавлены"
    fun blockedUsers(blocked: List<PhoneNumber>) = "вы пытались добавить заблокированных пользователей: " +
            blocked.joinToString(separator = ", ") { it.value }

    fun badFormat(errors: List<Int>) =
        "неправильный формат в ${errors.size} ${pluralize(errors.size, "строчке", "строчках", "строчках")} таблицы:" +
                errors.joinToString(separator = ", ") { it.toString() }

    fun pluralize(count: Int, one: String, few: String, many: String) = when {
        count % 10 == 1 && count % 100 != 11 -> one
        count % 10 in 2..4 && count % 100 !in 12..14 -> few
        else -> many
    }
}
