@file:Suppress("MaxLineLength")

package auth.telegram

import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import auth.domain.entities.User
import auth.telegram.Strings.AccountInfo.NoQuestionArea
import auth.telegram.Strings.Courses.EducationalProgramsCode
import auth.telegram.Strings.Courses.LeadersOfBreakthrough
import auth.telegram.Strings.Courses.ManagementSchool
import auth.telegram.Strings.Courses.RectorsSchool
import auth.telegram.Strings.Courses.StepToSchoolDevelopment
import common.telegram.strings.accountInfo
import dev.inmo.tgbotapi.utils.buildEntities
import dev.inmo.tgbotapi.utils.regularln
import qna.domain.entities.QuestionArea

object Strings {
    const val Welcome =
        "Дорогой участник, приветствуем тебя в боте сообщества выпускников программ Центра трансформации образования. " +
            "Здесь собрались выпускники разных лет таких программ, как Школа ректоров, Лидеры научно-технологического прорыва, Школа управления исследовательскими программами и других программ. " +
            "Для того, чтобы попасть в пространство единомышленников, поделитесь контактом, нажав соответствующую кнопку и заполните свой краткий профайл"
    const val ShareContact = "Поделиться номером телефона"
    const val InvalidShare = "Чтобы поделиться контактом, нажмите на кнопку из меню"

    object AccountInfo {
        const val WriteName = "Введите свои имя и фамилию"
        const val WriteProfession = "Напишите Вашу должность"
        const val ChooseProfessionalAreas =
            "Уточните Ваши профессиональные зоны компетенций (вы можете выбрать несколько)\n" +
                "Когда вы выберете все Ваши профессиональные зоны компетенций, нажмите \"Закончить выбор\"\n" +
                "\nРекомендуемое количество не более 5"

        const val NoQuestionArea =
            "Вы не выбрали ни одной сферы, интересующей вас. Необходимо выбрать хотя бы одну сферу."

        const val WriteProfessionalActivity =
            "Напишите о своей деятельности - что именно Вы делаете на работе, с какими задачами сталкиваетесь"
        const val PersonWantsAdd = "Пользователь хочет присоединиться к сообществу.\n"

        fun writePersonInfo(userDetails: User.Details) =
            buildEntities {
                regularln(PersonWantsAdd)
                addAll(accountInfo(userDetails))
            }

        const val Approved = "Новый пользователь был добавлен ✅"
        const val NotApproved = "Новый пользователь не был добавлен ❌"

        fun approvePersonInfo(userDetails: User.Details, boolean: Boolean) =
            buildEntities {
                regularln(if (boolean) Approved else NotApproved)
                addAll(accountInfo(userDetails))
            }
    }

    const val AccountWasVerified = "Ваш аккаунт успешно прошел верификацию"
    const val StartButton = "Начать работу"

    val AdminDoNotAccept = """
        |Ваша заявка не была одобрена администратором.
        |Если вы думаете, что произошла ошибка, напишите /start и заполните профайл ещё раз.
    """.trimMargin()

    const val FinishChoosing = "Закончить выбор"

    object OrganizationTypes {
        const val ChooseOrganizationType =
            "Выберите тип организации, в которой вы работаете"
        const val ScientificOrganization = "научная организация"
        const val School = "школа"
        const val EngineeringUniversity = "инженерно-технический университет"
        const val ClassicalUniversity = "классический (многопрофильный) университет"
        const val FederalUniversity = "федеральный университет"
        const val MedicalUniversity = "медицинский университет"
        const val AgriculturalUniversity = "аграрный университет"
        const val TransportUniversity = "транспортный университет"
        const val TheatreUniversity = "театральный институт"
    }

    var organizationTypeToString = mapOf<OrganizationType, String>(
        OrganizationType.ScientificOrganization to OrganizationTypes.ScientificOrganization,
        OrganizationType.School to OrganizationTypes.School,
        OrganizationType.EngineeringUniversity to OrganizationTypes.EngineeringUniversity,
        OrganizationType.ClassicalUniversity to OrganizationTypes.ClassicalUniversity,
        OrganizationType.FederalUniversity to OrganizationTypes.FederalUniversity,
        OrganizationType.MedicalUniversity to OrganizationTypes.MedicalUniversity,
        OrganizationType.AgriculturalUniversity to OrganizationTypes.AgriculturalUniversity,
        OrganizationType.TransportUniversity to OrganizationTypes.TransportUniversity,
        OrganizationType.TheatreUniversity to OrganizationTypes.TheatreUniversity
    )

    object Courses {
        const val ChooseCourse =
            "Выберите программу, которую вы прошли"
        const val RectorsSchool = "Школа ректоров"
        const val LeadersOfBreakthrough = "Лидеры научно-технологического прорыва"
        const val ManagementSchool = "Школа управления исследовательскими программами"
        const val StepToSchoolDevelopment = "Шаг развития школы"
        const val EducationalProgramsCode = "Код образовательных программ"
    }

    val courseToString = mapOf<Course, String>(
        Course.RectorsSchool to RectorsSchool,
        Course.LeadersOfBreakthrough to LeadersOfBreakthrough,
        Course.ManagementSchool to ManagementSchool,
        Course.StepToSchoolDevelopment to StepToSchoolDevelopment,
        Course.EducationalProgramsCode to EducationalProgramsCode
    )

    object Question {
        const val ChooseQuestionArea =
            "Выберите область, к которой относится Ваш вопрос (вы можете выбрать несколько)\n" +
                "Когда вы выберете все интересные вам сферы, нажмите \"Закончить выбор\"\n" +
                "\nРекомендуемое количество не более 5"
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
        const val OK = "Вы успешно зарегистрированы"
        const val RequiresApproval = "Ваш аккаунт на верификации"
        const val DuplicatePhoneNumber = "Аккаунт с вашим номером телефона уже существует. Обратитесь к администратору."
        const val AlreadyRegistered = "Вы уже зарегистрированы. Повторная регистрация невозможна."
        const val NoAreaSet = NoQuestionArea
    }

    object RoleMenu {
        const val Admin = "Меню администратора"
        const val Unauthenticated = "Неудачная попытка авторизации"
        const val Normal = "Меню пользователя"
    }

    object OldQuestion {
        const val ListClosedQuestions = "Нажмите на один из них, чтобы посмотреть всех, кто отвечал на данный вопрос"
        const val HaveNotOldQuestion = "На данный момент нет вопросов, на которые вы бы получили ответ"
        const val ListOfRespondents =
            "Список всех, кто отвечал на вопрос. Нажмите на одного из них, чтобы посмотреть контакт."
    }
}
