@file:Suppress("MaxLineLength")

package auth.telegram

import auth.telegram.Strings.AccountInfo.NoQuestionArea
import qna.domain.entities.QuestionArea

object Strings {
    const val Welcome =
        "Дорогой участник, приветствуем тебя в боте сообщества выпускников программ Центра трансформации образования. " +
            "Здесь собрались выпускники разных лет таких программ, как Школа ректоров, Лидеры научно-технологического прорыва, Школа управления исследовательскими программами и других программ. " +
            "Для того, чтобы попасть в пространство единомышленников, поделитесь контактом, нажав соответствующую кнопку и заполните свой краткий профайл"
    const val ShareContact = "Поделиться номером телефона"
    const val InvalidShare = "Чтобы поделиться контактом, нажмите на кнопку из меню"

    object AccountInfo {
        const val WriteName = "Введите своё имя"
        const val ChooseCountry = "Укажите из какой Вы страны"
        const val ChooseDistrict = "Укажите из какого Вы округа"
        const val ChooseRegion = "Укажите из какого Вы региона"
        const val ChooseCity =
            "Укажите из какого Вы города " // /мб надо будет поменять, с указанием, что надо выбрать из выпадающего списка
        const val WriteProfession = "Напишите Вашу должность"
        const val WriteOrganization = "Напишите название Вашей организации"
        const val ChooseProfessionalAreas = "Уточните ваши профессиональные зоны компетенций" // /возможность выбрать

        const val NoQuestionArea =
            "Вы не выбрали ни одной сферы, интересующей вас. Для регистрации необходимо выбрать хотя бы одну сферу."

        const val WriteProfessionalActivity =
            "Напишите о своей деятельности - что именно Вы делаете на работе, с какими задачами сталкиваетесь"
    }

    const val FinishChoosing = "Закончить выбор"

    object Question {
        const val ChooseQuestionArea =
            "Выберите область, к которой относится Ваш вопрос (вы можете выбрать несколько)\n" +
                "Когда вы выберете все интересные вам сферы, нажмите \"Закончить выбор\""
        const val Science = "наука"
        const val Education = "образование"
        const val Innovations = "инновации"
        const val Entrepreneurship = "предпринимательство"
        const val Finances = "финансы"
        const val Youngsters = "молодежь"
        const val Staff = "кадры"
        const val Campus = "кампус"
        const val Society = "общество"
        const val Strategy = "стратегия"
        const val Others = "иное"
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
        const val OK = "Вы успешно зарегистрированы."
        const val DuplicatePhoneNumber = "Аккаунт с вашим номером телефона уже существует. Обратитесь к администратору."
        const val AlreadyRegistered = "Вы уже зарегистрированы. Повторная регистрация невозможна."
        const val PhoneNumberNotAllowed = "Ваш номер телефона отсутствует в базе номеров. Обратитесь к администратору."
        const val NoAreaSet = NoQuestionArea
    }

    object RoleMenu {
        const val Admin = "Меню администратора"
        const val Unauthenticated = "Неудачная попытка авторизации."
        const val Normal = "Меню пользователя"
    }

    object OldQuestion {
        const val listClosedQuestions = "Нажмите на один из них, чтобы посмотреть всех, кто отвечал на данный вопрос."
        const val haveNotOldQuestion = "У вас нет вопросов на которые вы бы получили ответ."
        const val listOfDefendants = "Список всех кто отвечал на данный вопрос. Нажмите на одного из них, что бы " +
            "посмотреть контакт."
    }
}
