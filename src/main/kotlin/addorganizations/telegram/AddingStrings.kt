package addorganizations.telegram

import common.telegram.functions.StringsCity
import common.telegram.functions.StringsOrganization
import common.telegram.strings.DropdownWebAppStrings

@Suppress("TooManyFunctions")
object AddingStrings {

    const val Confirmation = "Добавить"
    const val InputCity = "Введите название города"
    const val InputOrganization = "Введите название организации"
    val CityDropdown = StringsCity(
        button = DropdownWebAppStrings.CityDropdown.button,
        confirmation = "Пожалуйста, убедитесь, что этого города действительно нет в списке.\n\n" +
            "Если вы подтвердите отсутствие города, вам будет предложено ввести его.",
        noCity = DropdownWebAppStrings.CityDropdown.noCity,
        chooseCity = "Проверьте отстутствие города в списке"
    )
    val CityOrganizationDropdown = StringsCity(
        button = DropdownWebAppStrings.CityDropdown.button,
        confirmation = "Пожалуйста, убедитесь, что этого города действительно нет в списке.\n\n" +
            "Если вы подтвердите отсутствие города, вы не сможете продолжить добавление организации.",
        noCity = DropdownWebAppStrings.CityDropdown.noCity,
        chooseCity = "Выберите город, в котором расположена организация"
    )
    val OrganizationDropdown = StringsOrganization(
        button = DropdownWebAppStrings.OrganizationDropdown.button,
        confirmation = "Пожалуйста, убедитесь, что этой организации действительно нет в списке.\n\n" +
            "Если вы подтвердите отсутствие организации, вам будет предложено ввести её.",
        noOrganization = DropdownWebAppStrings.OrganizationDropdown.noOrganization,
        chooseOrganization = "Просмотрите список организаций и проверьте её отсутствие"
    )
    private const val tipStart = "Если вы прервали регистрацию, введите /start."
    fun havingOrganizationAdmin(city: String, organization: String) = "Вы привязали организацию: $organization\n\n" +
        "к городу: $city."
    fun havingOrganizationUser(city: String, organization: String) = "Вашу организацию: $organization\n\n" +
        "привязали к городу: $city.\n\n$tipStart"
    fun havingCityAdmin(city: String) = "Вы выбрали существующий город: $city"
    fun havingCityUser(city: String) = "Ваш город нашли в таблице под названием: $city.\n\n$tipStart"
    fun addCityAdmin(city: String) = "Вы добавили город: $city."
    fun addCityUser(city: String) = "Ваш город добавили: $city.\n\n" +
        "Этот город теперь можно выбрать в своем профиле.$tipStart"
    fun addOrganizationAdmin(city: String, organization: String) = "Вы создали организацию: $organization\n\n" +
        "и привязали её к городу: $city."
    fun addOrganizationUser(city: String, organization: String) = "Вашу организацию добавили: $organization\n\n" +
        "и привязали её к городу: $city.\n\n$tipStart"
    fun confirmationAddingCityAdmin(city: String) =
        "Предложено добавить новый город: $city"
    fun sentCity(city: String) = "Вы отправили запрос на добавление города: $city.\n\n" +
        "Пожалуйста, подождите, пока администраторы добавят его, когда это произойдёт, вам прийдет уведомление и вы" +
        " сможете зарегистрироваться"
    fun sentOrganization(organization: String, city: String) = "Вы отправили запрос на добавление организации:" +
        " $organization.\n\nВ городе: $city\n\n" +
        "Пожалуйста, подождите, пока администраторы добавят её, когда это произойдёт, вам прийдет уведомление и вы" +
        " сможете зарегистрироваться"

    fun confirmationAddingOrganizationAdmin(having: Boolean, city: String, organization: String) =
        "${
            if (having) {
                "Предложено добавить в старый город: $city\n"
            } else {
                "Предложено добавить в новый город: $city\n\n" +
                    "Это значит, что выше должно быть предложение добавить этот город."
            }
        }\nОрганизацию: $organization"
}
