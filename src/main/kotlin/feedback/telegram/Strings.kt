package feedback.telegram

import auth.domain.entities.PhoneNumber
import dev.inmo.tgbotapi.utils.boldln
import dev.inmo.tgbotapi.utils.buildEntities
import dev.inmo.tgbotapi.utils.regular
import dev.inmo.tgbotapi.utils.regularln

object Strings {
    const val IfSuccessful = "Отлично, мы рады!"
    const val IfUnsuccessful = "Спасибо, отметили!"
    const val QuestionClosed = "Вопрос закрыт"
    const val QuestionNotClosed = "Хорошо!"
    fun feedbackRequest(name: String, phoneNumber: PhoneNumber) = buildEntities {
        regularln("Прошла неделя, удалось ли выйти на контакт с")
        boldln(name)
        regular(phoneNumber.toString())
        regularln("?")
    }

    fun shouldCloseQuestion(subject: String) =
        "Отлично, мы рады! Получили ли вы удовлетворительный ответ на свой вопрос «$subject»?"
}
