package addorganizations.telegram

import common.telegram.functions.StringsCity

object AddingStrings {

    const val Confirmation = "Добавить"
    const val InputCity = "Введите название города"
    const val InputUniversity = "Введите название университета"
    val CityDropdown = StringsCity(
        button = "Выбрать город",
        confirmation = "Пожалуйста, убедитесь, что этого города действительно нет в списке.\n\n" +
                "Если вы подтвердите отсутствие города, вам будет предложено ввести его",
        noCity = "такого города нет в списке",
        chooseCity = "Выберите город, нажав на кнопку"
    )
    fun confirmationAddingCityAdmin(city: String) =
        "Предлжено добавить новый город: $city\n"
    fun sentCity(city: String) = "Вы отправили запрос на добавление города: $city.\n" +
            "Пожалуйста, подождите, пока администраторы добавят его, когда это произойдёт вам прийдет уводамление и вы сможете зарегистрироваться"
    fun sentUniversity(university: String, city: String) = "Вы отправили запрос на добавление организации: $university.\n" +
            "В городе: $city\n" +
            "Пожалуйста, подождите, пока администраторы добавят её, когда это произойдёт вам прийдет уведомление и вы сможете зарегистрироваться"

    fun confirmationAddingUniversityAdmin(having: Boolean, city: String, university: String) =
        "${
            if (having) {
                "Предложено добавить в старый город: $city"
            } else {
                "Предложено добавить в новый город: $city"
            }
        }\nУниверситет: $university"

}