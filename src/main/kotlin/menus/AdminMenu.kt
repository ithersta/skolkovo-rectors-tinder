package menus

import auth.domain.entities.User
import auth.telegram.Strings
import common.telegram.DialogState
import common.telegram.strings.CommonStrings
import generated.menu
import menus.states.MenuState
import menus.strings.MenuStrings
import event.telegram.flows.sendListOfEvents

val adminMenu = menu<User.Admin>(Strings.RoleMenu.Admin, DialogState.Empty) {
    menu()
    submenu(MenuStrings.AdminMenu.Main, MenuStrings.AdminMenu.Description, MenuState.AdminMenuState) {
        button(
            MenuStrings.AdminMenu.AddCity,
            MenuState.AddCityState
        )
        button(
            MenuStrings.AdminMenu.AddUniversity,
            MenuState.AddUniversityState
        )
        button(
            MenuStrings.AdminMenu.AddEvent,
            MenuState.AddEventState
        )
        button(MenuStrings.AdminMenu.RemoveEvent){
            sendListOfEvents(it.chat.id)
        }
        backButton(CommonStrings.Button.Back)
    }
}
