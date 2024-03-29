package qna.telegram.strings

import auth.domain.entities.User
import auth.telegram.Strings
import common.telegram.strings.accountInfo
import dev.inmo.tgbotapi.utils.*
import qna.domain.usecases.NewResponseNotification
import qna.domain.entities.Question as DomainQuestion

object Strings {
    object QuestionToCurator {
        fun message(subject: DomainQuestion.Subject, questionText: DomainQuestion.Text) =
            buildEntities {
                regular("Добрый день, поступил новый вопрос ")
                bold("центру трансформации образования\n\n")
                boldln(subject.value)
                regularln(questionText.value)
            }
    }

    object ToAnswerUser {
        fun message(question: DomainQuestion) =
            buildEntities {
                regular(
                    "Добрый день, один из участников сообщества хотел бы " +
                        "выйти на коммуникацию по следующему вопросу:\n\n"
                )
                underline("сферы вопроса")
                regular(": ")

                regularln(question.areas.joinToString { Strings.questionAreaToString.getValue(it) })

                regularln("")

                boldln(question.subject.value)
                regularln(question.text.value + "\n")
                boldln("Готовы ответить?")
            }

        fun editMessage(question: DomainQuestion) =
            buildEntities {
                regular("Вы согласились ответить на вопрос:\n\n")
                boldln(question.subject.value)
                regularln(question.text.value)
            }

        fun waitingForCompanion(subject: DomainQuestion.Subject) =
            buildEntities {
                regular("Владелец вопроса ")
                bold("\"${subject.value}\"")
                regular(" свяжется с Вами")
            }

        const val SentAgreement = "Спасибо, Ваше согласие направлено владельцу вопроса. Ожидаем ответ."
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
        fun message(notification: NewResponseNotification) = buildEntities {
            regular("Есть участники, согласившиеся ответить вам на вопрос ")
            bold("«${notification.question.subject}»")
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

    fun question(question: DomainQuestion) = buildEntities {
        if (question.isClosed) regularln("❌ Вопрос закрыт")
        boldln(question.subject.value)
        regularln(question.text.value)
    }

    fun respondedQuestion(question: qna.domain.entities.Question) = buildEntities {
        regularln("✅ Владелец вопроса свяжется с Вами.")
        addAll(question(question))
    }

    object OldQuestion {
        const val ListClosedQuestions = "Нажмите на один из них, чтобы посмотреть всех, кто отвечал на данный вопрос"
        const val HaveNotOldQuestion = "На данный момент нет вопросов, на которые вы бы получили ответ"
        const val ListOfRespondents =
            "Список всех, кто отвечал на вопрос. Нажмите на одного из них, чтобы посмотреть контакт."
    }

    const val QuestionAreasList = "Список сфер по вашему профилю. Нажмите на сферу, чтобы посмотреть список вопросов."
    const val InterestingQuestionsList = "Список вопросов по вашим сферам. Нажмите на тему, чтобы посмотреть подробнее."
    const val NoInterestingQuestions = "На данный момент нет вопросов по этой сфере"
}
