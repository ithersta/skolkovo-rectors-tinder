package feedback.telegram

object Strings {
    const val IfSuccessful = "Отлично, мы рады!"
    const val IfUnsuccessful = "Спасибо, отметили!"
    const val QuestionClosed = "Вопрос закрыт"
    const val QuestionNotClosed = "Хорошо!"
    fun feedbackRequest(name: String) = "Прошла неделя, удалось ли выйти на контакт с $name?"
    fun shouldCloseQuestion(subject: String) = "Отлично, мы рады! Закрыть вопрос «$subject»?"
}
