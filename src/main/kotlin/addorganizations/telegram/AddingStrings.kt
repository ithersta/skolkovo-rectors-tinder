package addorganizations.telegram

import common.telegram.functions.StringsCity
import common.telegram.functions.StringsOrganization

@Suppress("TooManyFunctions")
object AddingStrings {

    const val Confirmation = "Добавить"
    const val InputCity = "Введите название города"
    const val InputOrganization = "Введите название организации"
    val CityDropdown = StringsCity(
        button = "Выбрать город",
        confirmation = "Пожалуйста, убедитесь, что этого города действительно нет в списке.\n\n" +
            "Если вы подтвердите отсутствие города, вам будет предложено ввести его.",
        noCity = "такого города нет в списке",
        chooseCity = "Выберите город, нажав на кнопку"
    )
    val CityOrganizationDropdown = StringsCity(
        button = "Выбрать город",
        confirmation = "Пожалуйста, убедитесь, что этого города действительно нет в списке.\n\n" +
            "Если вы подтвердите отсутствие города, вы не сможете продолжить добавление организации.",
        noCity = "такого города нет в списке",
        chooseCity = "Выберите город, нажав на кнопку"
    )
    val OrganizationDropdown = StringsOrganization(
        button = "Выбрать организацию",
        confirmation = "Пожалуйста, убедитесь, что этой организации действительно нет в списке.\n\n" +
            "Если вы подтвердите отсутствие организации, вам будет предложено ввести её.",
        noOrganization = "этой организации нет в списке",
        chooseOrganization = "Выберите организацию, нажав на кнопку"
    )
    fun havingOrganizationAdmin(city: String, organization: String) = "Вы привязали организацию: $organization\n" +
        "к городу: $city."
    fun havingOrganizationUser(city: String, organization: String) = "Вашу организацию: $organization\n" +
        "привязали к городу: $city.\n" +
        "Чтобы зарегистрироваться введите /start."
    fun havingCityAdmin(city: String) = "Вы выбрали существующий город: $city"
    fun havingCityUser(city: String) = "Ваш город нашли в таблице под названием: $city.\n" +
        "Чтобы зарегистрироваться введите /start."
    fun addCityAdmin(city: String) = "Вы добавили город: $city."
    fun addCityUser(city: String) = "Ваш город добавили: $city.\n" +
        "Этот город теперь можно выбрать в своем профиле." +
        "Чтобы зарегистрироваться введите /start."
    fun addOrganizationAdmin(city: String, organization: String) = "Вы создали организацию: $organization\n" +
        "и привязали её к городу: $city."
    fun addOrganizationUser(city: String, organization: String) = "Вашу организацию добавили: $organization\n" +
        "и привязали её к городу: $city.\n" +
        "Чтобы зарегистрироваться введите /start."
    fun confirmationAddingCityAdmin(city: String) =
        "Предложено добавить новый город: $city\n"
    fun sentCity(city: String) = "Вы отправили запрос на добавление города: $city.\n" +
        "Пожалуйста, подождите, пока администраторы добавят его, когда это произойдёт вам прийдет уведомление и вы" +
        " сможете зарегистрироваться"
    fun sentOrganization(organization: String, city: String) = "Вы отправили запрос на добавление организации:" +
        " $organization.\nВ городе: $city\n" +
        "Пожалуйста, подождите, пока администраторы добавят её, когда это произойдёт вам прийдет уведомление и вы" +
        " сможете зарегистрироваться"

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
