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
                regular("Владелец вопроса ")
                bold("\"$subject\"")
                regular(" свяжется с Вами.")
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

    object ToAskUser {
        fun message(name: String, city: String, job: String, organization: String, activityDescription: String) =
            buildEntities {
                regularln("Профиль участника сообщества, согласившегося ответить вам:\n")
                addAll(accountInfo(name, city, job, organization, activityDescription))
                boldln("Вы согласны пообщаться?")
            }

        const val WriteToCompanion = "Напишите сразу собеседнику, чтобы договориться о времени " +
            "и формате встречи - онлайн или оффлайн. А через неделю мы спросим Вас как все прошло."
        val CopyQuestion = buildEntities { bold("Скопируйте вопрос для отправки собеседнику") }
    }

    object Question {
        const val SubjectQuestion = "Напишите тему вопроса (краткая формулировка)"
        const val WordingQuestion = "Сформулируйте свой вопрос"
        const val AskingQuestionIntent = "Выберите цель вопроса"
        const val InvalidQuestionIntent = "Пожалуйста, выберите цель вопроса из кнопочного меню"
        const val CompletedQuestion = "Отлично, вопрос сформирован!"
        const val Success = "Вопрос успешно отправлен в сообщество!"
    }

    object RespondentsNoAnswer {
        // тут мб по-другому написать
        const val ListOfSubjects = "Список Ваших вопросов.\n" +
            "Нажмите на один из них для просмотра списка участников, " +
            "которые согласились ответить Вам на вопрос или для закрытия вопроса."
        const val ChooseAction = "Выберите действие:"
        const val CloseQuestionSuccessful = "Вопрос успешно закрыт!"
        const val ListOfRespondents = "Список участников, которые согласились ответить на Ваш вопрос.\n" +
            "Нажмите на одного из них для просмотра информации о профиле."

        const val NoQuestions = "На данный момент у Вас нет актуальных вопросов"
        const val NoRespondent = "На данный момент нет людей, которые ответили бы на Ваш вопрос"
    }

    object NewResponses {
        fun message(notification: NewResponseNotification) = when (notification) {
            is NewResponseNotification.Daily -> buildEntities {
                regular("Есть участники, согласившиеся ответить вам на вопрос ")
                bold("«${notification.question.subject}»")
            }

            is NewResponseNotification.OnThreshold -> buildEntities {
                regular("${notification.count} участника согласились ответить вам на вопрос ")
                bold("«${notification.question.subject}»")
            }
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

    object TargetArea {
        const val ListSpheres = "Список сфер по вашему профилю. Нажмите на сферу, чтобы посмотреть список тем вопросов."
        const val ListQuestion = "Список вопросов по вашим сферам. Нажмите на тему, чтобы посмотреть подробнее."
        const val HaveNotQuestionInThisArea = "На данный момент нет вопросов по этой сфере."
        fun buildQuestionByQuestionText(text: String): String {
            return "Текст вопроса $text.\nХотите ли вы ответить на этот вопрос?"
        }
    }
}
