package strings

object Strings {
    object ToAnswerUser {
        fun message(question: String) = "Добрый день, один из участников сообщества хотел бы " +
            "выйти в коммуникацию по следующему вопросу: $question. Готовы ответить?"
    }
    object ToAskUser {
        fun message(profile: String) = "Профиль участника сообщества, согласившегося ответить вам - " +
            "$profile Вы согласны пообщаться?"
    }
    const val WriteToCompanion = "Напишите сразу собеседнику, чтобы договориться о времени " +
        "и формате встречи - онлайн или оффлайн. А через неделю мы напишем Вам как все прошло."
    const val CopyQuestion = "Скопируйте вопрос для отправки собеседнику"
    const val SentAgreement = "Спасибо, Ваше согласие направлено владельцу вопроса. Ожидаем ответ."
    const val WaitingForCompanion = "Владелец вопроса свяжется с Вами."
    const val QuestionResolved = "Спасибо за готовность помочь, кто-то оказался быстрее, и вопрос уже решен!"
}
