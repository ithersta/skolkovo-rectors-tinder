package common.telegram.strings

import common.telegram.functions.StringsCity
import common.telegram.functions.StringsOrganization

object DropdownWebAppStrings {
    val CityDropdown =  StringsCity(
        button = "Выбрать город",
        confirmation = "Пожалуйста, убедитесь, что вашего города действительно нет в списке.\n\n" +
            "Если вы подтвердите отсутствие города, вам будет предложено ввести его, " +
            "и регистрация приостановится до " +
            "момента его подтверждения администратором.",
        noCity = "моего города нет в списке",
        chooseCity = "Выберите свой город, нажав на кнопку"
    )
    val organizationDropdown = StringsOrganization(
        button = "Выбрать организацию",
        confirmation = "Пожалуйста, убедитесь, что вашей организации действительно нет в списке.\n\n" +
                "Если вы подтвердите отсутствие организации, вам будет предложено ввести её, " +
                "и регистрация приостановится до " +
                "момента её подтверждения администратором.",
        noOrganization = "моей организации нет в списке",
        chooseOrganization = "Выберите ваше место работы, нажав на кнопку"
    )
}
