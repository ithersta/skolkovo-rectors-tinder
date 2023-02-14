package menus

import auth.domain.entities.User
import auth.telegram.Strings
import com.ithersta.tgbotapi.menu.builders.menu
import common.telegram.DialogState
import states.MenuState

val adminMenu = menu<DialogState, User, User.Admin>(Strings.RoleMenu.Normal, DialogState.Empty) {
    extracted()
    button(
        Strings.MenuButtons.AddUser,
        MenuState.AddUser
    ) // /ну видимо хендлер надо тоже или стейт нормальный реализовать
    button(
        Strings.MenuButtons.Notifications.Main,
        MenuState.Notifications
    ) // /TODO: в этом стейте Ивану реализовать логику вывода одной кнопки: "приостановить"
    // /если оповещения включены, и "возобновить", если оповещения выключены
}
