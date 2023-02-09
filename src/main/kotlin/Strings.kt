import fillingAccountInfo.QuestionArea

object Strings {
    object AccountInfo {
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
        QuestionArea.Innovations to Question.Innovations,
        QuestionArea.Entrepreneurship to Question.Entrepreneurship,
        QuestionArea.Finances to Question.Finances,
        QuestionArea.Youngsters to Question.Youngsters,
        QuestionArea.Staff to Question.Staff,
        QuestionArea.Campus to Question.Campus,
        QuestionArea.Society to Question.Society,
        QuestionArea.Strategy to Question.Strategy,
        QuestionArea.Others to Question.Others,
    )
    var stringToQuestionArea = questionAreaToString.map { it.value to it.key }.toMap()
}
