package common.telegram.strings

import auth.domain.entities.User
import dev.inmo.tgbotapi.types.message.textsources.TextSourcesList
import dev.inmo.tgbotapi.utils.bold
import dev.inmo.tgbotapi.utils.buildEntities
import dev.inmo.tgbotapi.utils.regularln

object CommonStrings {
    object Button {
        const val Yes = "Да"
        const val No = "Нет"
        const val Back = "◀️ Назад"
    }

    const val InternalError = "Произошла внутренняя ошибка бота"

    fun maxLengthExceeded(maxLength: Int) =
        "Максимальная длина $maxLength превышена. Сократите сообщение и попробуйте ещё раз."
}

fun accountInfo(userDetails: User.Details): TextSourcesList {
    return buildEntities {
        bold("Имя: ")
        regularln(userDetails.name.value)
        bold("Город: ")
        regularln(userDetails.city.name)
        bold("Должность: ")
        regularln(userDetails.job)
        bold("Организация: ")
        regularln(userDetails.organization.name)
        bold("Деятельность: ")
        regularln(userDetails.activityDescription)
        regularln("")
    }
}
