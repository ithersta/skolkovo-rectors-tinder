package qna.strings

import dev.inmo.tgbotapi.utils.*

object Strings {
    object ToAnswerUser {
        fun message(subject: String, question: String) =
            buildEntities {
                regular("Добрый день, один из участников сообщества хотел бы выйти в коммуникацию по следующему вопросу:\n\n")
                boldln(subject)
                regularln(question + "\n")
                boldln("Готовы ответить?")
            }
        const val SentAgreement = "Спасибо, Ваше согласие направлено владельцу вопроса. Ожидаем ответ."
        const val WaitingForCompanion = "Владелец вопроса свяжется с Вами."
        const val QuestionResolved = "Спасибо за готовность помочь, кто-то оказался быстрее, и вопрос уже решен!"
    }

    object ToAskUser {
        // TODO: можно что-то добавить(emoji например)?
        // Профиль состоит из:
        // Имя
        // Город
        // Должность
        // Организация
        // Профессиональные зоны компетенции
        // Деятельность
        fun message(name: String, city: String, job: String, organisation: String, activityDescr: String) =
            buildEntities {
                regularln("Профиль участника сообщества, согласившегося ответить вам:\n")
                bold("Имя: ")
                regularln(name)
                bold("Город: ")
                regularln(city)
                bold("Должность: ")
                regularln(job)
                bold("Организация: ")
                regularln(organisation)
                bold("Деятельность: ")
                regularln(activityDescr)
                regularln("")
                boldln("Вы согласны пообщаться?")
            }
        const val WriteToCompanion = "Напишите сразу собеседнику, чтобы договориться о времени " +
            "и формате встречи - онлайн или оффлайн. А через неделю мы напишем Вам как все прошло."
        val CopyQuestion = buildEntities{bold("Скопируйте вопрос для отправки собеседнику")}
    }

    object Question {
        object Intent {
            const val TestHypothesis = "Хочу проверить гипотезу (посоветоваться)"
            const val Consultation = "У меня есть запрос на консультацию по теме"
            const val FreeForm = "Хочу задать вопрос участникам сообщества (в свободной форме)"
        }

        const val SubjectQuestion = "Напишите тему вопроса (краткая формулировка)"
        const val WordingQuestion = "Сформулируйте свой вопрос"
        const val AskingQuestionIntent = "Выберите цель вопроса"
        const val InvalidQuestionIntent = "Пожалуйста, выберите цель вопроса из кнопочного меню"
        const val CompletedQuestion = "Отлично, вопрос сформирован!"
        const val Success = "Вопрос успешно отправлен в сообщество"
    }
}
