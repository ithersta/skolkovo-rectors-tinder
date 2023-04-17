package menus

import addorganizations.telegram.states.CheckCityAdminState
import addorganizations.telegram.states.ChooseCityOrganizationAdminState
import auth.domain.entities.User
import auth.telegram.Strings
import common.telegram.DialogState
import common.telegram.strings.CommonStrings
import event.telegram.flows.sendListOfEvents
import generated.menu
import menus.states.MenuState
import menus.strings.MenuStrings

val adminMenu = menu<User.Admin>(Strings.RoleMenu.Admin, DialogState.Empty) {
    menu()
    submenu(MenuStrings.AdminMenu.Main, MenuStrings.AdminMenu.Description, MenuState.AdminMenuState) {
        button(
            MenuStrings.AdminMenu.AddCity,
            CheckCityAdminState(null)
        )
        button(
            MenuStrings.AdminMenu.AddUniversity,
            ChooseCityOrganizationAdminState(null)
        )
        button(
            MenuStrings.AdminMenu.AddEvent,
            MenuState.AddEventState
        )
        button(MenuStrings.AdminMenu.RemoveEvent) {
            sendListOfEvents(it.chat.id)
        }
        backButton(CommonStrings.Button.Back)
    }
}
