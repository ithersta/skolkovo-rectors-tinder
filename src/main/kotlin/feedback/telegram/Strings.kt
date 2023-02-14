package feedback.telegram

object Strings {
    const val IfSuccessful = "Отлично, мы рады!"
    const val IfUnsuccessful = "Спасибо, отметили!"
    fun feedbackRequest(name: String) = "Прошла неделя, удалось ли выйти на контакт с $name?"
}
