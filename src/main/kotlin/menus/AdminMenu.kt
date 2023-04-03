package menus

import auth.domain.entities.User
import auth.telegram.Strings
import common.telegram.DialogState
import generated.menu
import menus.states.MenuState
import menus.strings.MenuStrings

val adminMenu = menu<User.Admin>(Strings.RoleMenu.Admin, DialogState.Empty) {
    menu()
    button(
        MenuStrings.AdminMenu.AddUser,
        MenuState.AddUser
    )
}
