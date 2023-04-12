package addorganizations.telegram

import common.telegram.functions.StringsCity
import common.telegram.functions.StringsOrganization

object AddingStrings {

    const val Confirmation = "Добавить"
    const val InputCity = "Введите название города"
    const val InputUniversity = "Введите название университета"
    val CityDropdown = StringsCity(
        button = "Выбрать город",
        confirmation = "Пожалуйста, убедитесь, что этого города действительно нет в списке.\n\n" +
                "Если вы подтвердите отсутствие города, вам будет предложено ввести его.",
        noCity = "такого города нет в списке",
        chooseCity = "Выберите город, нажав на кнопку"
    )
    val OrganizationDropdown = StringsOrganization(
        button = "Выбрать организацию",
        confirmation = "Пожалуйста, убедитесь, что этой организации действительно нет в списке.\n\n" +
                "Если вы подтвердите отсутствие организации, вам будет предложено ввести её.",
        noOrganization = "этой организации нет в списке",
        chooseOrganization = "Выберите место работы, нажав на кнопку"
    )
    fun confirmationAddingCityAdmin(city: String) =
        "Предлжено добавить новый город: $city\n"
    fun sentCity(city: String) = "Вы отправили запрос на добавление города: $city.\n" +
            "Пожалуйста, подождите, пока администраторы добавят его, когда это произойдёт вам прийдет уведомление и вы сможете зарегистрироваться"
    fun sentOrganization(organization: String, city: String) = "Вы отправили запрос на добавление организации: $organization.\n" +
            "В городе: $city\n" +
            "Пожалуйста, подождите, пока администраторы добавят её, когда это произойдёт вам прийдет уведомление и вы сможете зарегистрироваться"

    fun confirmationAddingOrganizationAdmin(having: Boolean, city: String, organization: String) =
        "${
            if (having) {
                "Предложено добавить в старый город: $city"
            } else {
                "Предложено добавить в новый город: $city\n" + 
                        "Это значит, что выше должно быть предложение добавить этот город."
            }
        }\nОрганизацию: $organization"

}