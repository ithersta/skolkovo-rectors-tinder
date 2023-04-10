package adduniversity.telegram

object Strings {
    const val confirmation = "Добавить"
    const val addCity = "Введите название города"
    const val addUniversity = "Введите название университета"

    fun confirmationAddingCityAdmin(city: String) =
        "Предлжено добавить новый город: $city\n"

    fun confirmationAddingUniversityAdmin(having: Boolean, city: String, university: String) =
        "${
            if (having) {
                "Предложено добавить в старый город: $city"
            } else {
                "Предложено добавить в новый город: $city"
            }
        }\nУниверситет: $university"

}