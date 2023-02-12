object Strings {
    object AccountInfo {
        val ChooseCountry = "Укажите из какой Вы страны"
        val ChooseDistrict = "Укажите из какого Вы округа"
        val ChooseRegion = "Укажите из какого Вы региона"
        val ChooseCity =
            "Укажите из какого Вы города" // /мб надо будет поменять, с указанием, что надо выбрать из выпадающего списка
        val WriteProfession = "Напишите Вашу должность"
        val WriteOrganization = "Напишите название Вашей организации"
        val ChooseProfessionalAreas = "Уточните ваши профессиональные зоны компетенций" // /возможность выбрать

        // из существующих блоков,
        // и оставить "Другое" для ответов в свободной форме
        val WriteProfessionalActivity =
            "Напишите о своей деятельности - что именно Вы делаете на работе, с какими задачами сталкиваетесь"
    }

    object Question {
        val ChooseQuestionArea =
            "Выберите область, к которой относится Ваш вопрос (множественный выбор из областей)\n" +
                    "Области:"
        var QuestionAreas = listOf(
            "1) наука",
            "2) образование",
            "3) инновации",
            "4) предпринимательство",
            "5) финансы",
            "6) молодежь",
            "7) кадры",
            "8) кампус",
            "9) общество",
            "10) стратегия",
            "11) иное"
        )
    }

    object MeetingInfo {
        val linkInEvent: String = "Ссылка на метроприятия: https://www.skolkovo.ru/navigator/events/"
    }
}
