package feedback.telegram

import dev.inmo.tgbotapi.utils.boldln
import dev.inmo.tgbotapi.utils.buildEntities
import dev.inmo.tgbotapi.utils.regular
import dev.inmo.tgbotapi.utils.regularln
import feedback.domain.entities.FeedbackRequest
import qna.domain.entities.Question

object Strings {
    const val IfSuccessful = "Отлично, мы рады!"
    const val IfUnsuccessful = "Спасибо, отметили!"
    const val QuestionClosed = "Вопрос закрыт"
    const val QuestionNotClosed = "Хорошо!"
    fun feedbackRequest(feedbackRequest: FeedbackRequest) = buildEntities {
        regularln("Прошла неделя, удалось ли выйти на контакт с")
        boldln(feedbackRequest.respondentName)
        regularln(feedbackRequest.respondentPhoneNumber.toString())
        regular(feedbackRequest.respondentOrganization)
        regularln("?")
    }

    fun shouldCloseQuestion(subject: Question.Subject) =
        "Отлично, мы рады! Получили ли вы удовлетворительный ответ на свой вопрос «${subject.value}»?"
}
