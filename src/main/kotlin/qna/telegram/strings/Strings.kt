package qna.telegram.strings

import auth.domain.entities.User
import dev.inmo.tgbotapi.types.message.textsources.TextSourcesList
import dev.inmo.tgbotapi.utils.*
import qna.domain.usecases.NewResponseNotification

object Strings {
    object ToAnswerUser {
        fun message(subject: String, question: String) =
            buildEntities {
                regular(
                    "Добрый день, один из участников сообщества хотел бы " +
                            "выйти на коммуникацию по следующему вопросу:\n\n"
                )
                boldln(subject)
                regularln(question + "\n")
                boldln("Готовы ответить?")
            }

        fun editMessage(subject: String, question: String) =
            buildEntities {
                regular("Вы согласились ответить на вопрос:\n\n")
                boldln(subject)
                regularln(question)
            }

        fun waitingForCompanion(subject: String) =
            buildEntities {
                regular("Владелец вопроса")
                bold("\"$subject\"")
                regular("свяжется с Вами.")
            }

        const val SentAgreement = "Спасибо, Ваше согласие направлено владельцу вопроса. Ожидаем ответ."
        const val QuestionResolved = "Спасибо за готовность помочь, кто-то оказался быстрее, и вопрос уже решен!"
    }

    fun accountInfo(userDetails: User.Details): TextSourcesList {
        return buildEntities {
            bold("Имя: ")
            regularln(userDetails.name)
            bold("Город: ")
            regularln(userDetails.city)
            bold("Должность: ")
            regularln(userDetails.job)
            bold("Организация: ")
            regularln(userDetails.organization)
            bold("Деятельность: ")
            regularln(userDetails.activityDescription)
            regularln("")
        }
    }

    object Question {
        const val SubjectQuestion = "Напишите тему вопроса (краткая формулировка)"
        const val WordingQuestion = "Сформулируйте свой вопрос"
        const val AskingQuestionIntent = "Выберите цель вопроса"
        const val InvalidQuestionIntent = "Пожалуйста, выберите цель вопроса из кнопочного меню"
        const val CompletedQuestion = "Отлично, вопрос сформирован!"
        const val Success = "Вопрос успешно отправлен в сообщество"
    }

    object NewResponses {
        fun message(notification: NewResponseNotification) = when (notification) {
            is NewResponseNotification.Daily ->
                "Есть участники, согласившиеся ответить вам на вопрос «${notification.question.subject}»"

            is NewResponseNotification.OnThreshold ->
                "${notification.count} участника согласились ответить вам на вопрос «${notification.question.subject}»"
        }

        fun profile(userDetails: User.Details) =
            buildEntities {
                regularln("Профиль участника сообщества, согласившегося ответить вам:\n")
                addAll(accountInfo(userDetails))
                boldln("Вы согласны пообщаться?")
            }

        fun acceptedProfile(userDetails: User.Details) =
            buildEntities {
                regularln("✅Вы согласились пообщаться с:\n")
                addAll(accountInfo(userDetails))
            }

        const val SeeButton = "Посмотреть"
        const val NextButton = "Следующий"
        const val AcceptButton = "Принять"
        const val NoMoreResponses = "Вы посмотрели всех откликнувшихся участников"
        const val WriteToCompanion = "Напишите сразу собеседникам, чтобы договориться о времени " +
                "и формате встречи - онлайн или оффлайн. А через неделю мы спросим Вас как все прошло."
        val CopyQuestion = buildEntities { bold("Скопируйте вопрос для отправки собеседникам") }
    }
}
