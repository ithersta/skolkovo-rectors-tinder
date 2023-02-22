package auth.telegram

import auth.telegram.Strings.AccountInfo.NoQuestionArea
import qna.domain.entities.QuestionArea

object Strings {
    val Welcome =
        "Дорогой участник, приветствуем тебя в боте сообщества выпускников." +
            " Для того, чтобы попасть в пространство единомышленников поделитесь контактом, нажав соответсвующую кнопку"
    val ShareContact = "Поделиться номером телефона"
    val InvalidShare = "Чтобы поделиться контактом, нажмите на кнопку из меню"

    object AccountInfo {
        val ChooseCountry = "Укажите из какой Вы страны"
        val ChooseDistrict = "Укажите из какого Вы округа"
        val ChooseRegion = "Укажите из какого Вы региона"
        val WriteName = "Введите своё имя"
        val ChooseCity =
            "Укажите из какого Вы города " // /мб надо будет поменять, с указанием, что надо выбрать из выпадающего списка
        val WriteProfession = "Напишите Вашу должность"
        val WriteOrganization = "Напишите название Вашей организации"
        val ChooseProfessionalAreas = "Уточните ваши профессиональные зоны компетенций" // /возможность выбрать

        var professionalAreas = listOf(
            "какая-то одна сфера",
            "какая-то другая сфера",
            "какая-то третья сфера",
            "Другое"
        )

        val WriteProfessionalArea = "Введите свою зону профессиональных компетенций"
        val NoProfessionalArea =
            "Вы не выбрали ни одной сферы профессионаьных компетенций. Для регистрации необходимо выбрать хотя бы одну сферу."
        val NoQuestionArea =
            "Вы не выбрали ни одной сферы, интересующей вас. Для регистрации необходимо выбрать хотя бы одну сферу."

        val WriteProfessionalActivity =
            "Напишите о своей деятельности - что именно Вы делаете на работе, с какими задачами сталкиваетесь"
    }

    val FinishChoosing = "Закончить выбор"

    object Question {
        val ChooseQuestionArea =
            "Выберите область, к которой относится Ваш вопрос (вы можете выбрать несколько)\n" +
                "Когда вы выберете все интересные вам сферы, нажмите \"Закончить выбор\""
        val Science = "наука"
        val Education = "образование"
        val Innovations = "инновации"
        val Entrepreneurship = "предпринимательство"
        val Finances = "финансы"
        val Youngsters = "молодежь"
        val Staff = "кадры"
        val Campus = "кампус"
        val Society = "общество"
        val Strategy = "стратегия"
        val Others = "иное"
    }

    var questionAreaToString = mapOf<QuestionArea, String>(
        QuestionArea.Science to Question.Science,
        QuestionArea.Education to Question.Education,
        QuestionArea.Innovation to Question.Innovations,
        QuestionArea.Entrepreneurship to Question.Entrepreneurship,
        QuestionArea.Finance to Question.Finances,
        QuestionArea.Youth to Question.Youngsters,
        QuestionArea.HumanResources to Question.Staff,
        QuestionArea.Campus to Question.Campus,
        QuestionArea.Society to Question.Society,
        QuestionArea.Strategy to Question.Strategy,
        QuestionArea.Other to Question.Others,
    )

    object AuthenticationResults {
        val OK = "Вы успешно зарегистрированы."
        val DuplicatePhoneNumber = "Аккаунт с вашим номером телефона уже существует. Обратитесь к администратору."
        val AlreadyRegistered = "Вы уже зарегистрированы. Повторная регистрация невозможна."
        val PhoneNumberNotAllowed = "Ваш номер телефона отсутствует в базе номеров. Обратитесь к администратору."
        val NoAreaSet = NoQuestionArea
    }

    object RoleMenu {
        val Admin = "Меню администратора"
        val Unauthenticated = "Неудачная попытка авторизации."
        val Normal = "Меню пользователя"
    }

    object MenuButtons {
        val AddUser = "Добавить пользователей"

        object Questions {
            val Question = "Вопрос"
            val QuestionDesciption = "Выберите, что вы хотите сделать"
            val Ask = "Задать вопрос сообществу"
            val Get = "Получить список вопросов"
            val Description = "Выберите категорию вопроса"

            object MyQuestions {
                val Main = "Мои вопросы"
                val Description = "Выберите тип вопроса"
                val ActualQuestions = "Актуальные"
                val OldQuestions = "Старые(закрытые)"
            }

            val InterestingQuestions = "По сферам"
        }

        object Notifications {
            val Main = "Оповещения"
            val Description = "Выберите, что вы хотите сделать"
            val On = "Включить"
            val Off = "Выключить"
        }

        val ChangeAccountInfo = "Изменить данные аккаунта"
        val Events = "Получить календарь мероприятий"
    }
    object TargetArea {
        val Yes = "Да"
        val No = "Нет"
        val ListQuestion = "Список вопросов по вашей сфере, нажмите на тему что бы посмотреть в чем суть вопроса."
        val Good = "Хорошо"
        val AnswerToPersonWhoAskedQuestion = "Один из пользователей площадки решил ответить на ваш вопрос свяжитесь с ним."
        val ReplyToRespondent = "Владелец вопроса свяжется с вами если сочтет это нужным"
    }
}
