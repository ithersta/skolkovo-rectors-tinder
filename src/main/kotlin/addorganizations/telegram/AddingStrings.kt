@file:Suppress("MaxLineLength")

package addorganizations.telegram

import common.telegram.functions.StringsCity
import common.telegram.functions.StringsOrganization
import common.telegram.strings.DropdownWebAppStrings
import organizations.domain.entities.City
import organizations.domain.entities.Organization

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
        chooseCity = "Проверьте отсутствие города в списке"
    )
    val CityOrganizationDropdown = StringsCity(
        button = DropdownWebAppStrings.CityDropdown.button,
        confirmation = null,
        noCity = null,
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
    fun havingOrganizationAdmin(city: City.Name, organization: Organization.Name) = "Вы привязали организацию: ${organization.value}\n\n" +
        "к городу: ${city.value}."
    fun havingOrganizationUser(city: City.Name, organization: Organization.Name) = "Вашу организацию: ${organization.value}\n\n" +
        "привязали к городу: ${city.value}.\n\n$tipStart"
    fun havingCityAdmin(city: City.Name) = "Вы выбрали существующий город: ${city.value}"
    fun havingCityUser(city: City.Name) = "Ваш город нашли в таблице под названием: ${city.value}.\n\n$tipStart"
    fun addCityAdmin(city: City.Name) = "Вы добавили город: ${city.value}."
    fun addCityUser(city: City.Name) = "Ваш город добавили: ${city.value}.\n\n" +
        "Этот город теперь можно выбрать в своем профиле.$tipStart"
    fun addOrganizationAdmin(city: City.Name, organization: Organization.Name) = "Вы создали организацию: ${organization.value}\n\n" +
        "и привязали её к городу: ${city.value}."
    fun addOrganizationUser(city: City.Name, organization: Organization.Name) = "Вашу организацию добавили: ${organization.value}\n\n" +
        "и привязали её к городу: ${city.value}.\n\n$tipStart"
    fun confirmationAddingCityAdmin(city: City.Name) =
        "Предложено добавить новый город: ${city.value}"
    fun sentCity(city: City.Name) = "Вы отправили запрос на добавление города: ${city.value}.\n\n" +
        "Пожалуйста, подождите, пока администраторы добавят его, когда это произойдёт, вам прийдет уведомление и вы" +
        " сможете зарегистрироваться"
    fun sentOrganization(organization: Organization.Name, city: City.Name) = "Вы отправили запрос на добавление организации:" +
        " ${organization.value}.\n\nВ городе: ${city.value}\n\n" +
        "Пожалуйста, подождите, пока администраторы добавят её, когда это произойдёт, вам прийдет уведомление и вы" +
        " сможете зарегистрироваться"

    fun confirmationAddingOrganizationAdmin(having: Boolean, city: City.Name, organization: Organization.Name) =
        "${
            if (having) {
                "Предложено добавить в старый город: ${city.value}\n"
            } else {
                "Предложено добавить в новый город: ${city.value}\n\n" +
                    "Это значит, что выше должно быть предложение добавить этот город."
            }
        }\nОрганизацию: ${organization.value}"
}
