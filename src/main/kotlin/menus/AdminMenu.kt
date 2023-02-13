package menus

import auth.domain.entities.User
import auth.telegram.Strings
import common.telegram.DialogState
import generated.menu
import states.MenuState

val adminMenu = menu<User.Admin>(Strings.RoleMenu.Normal, DialogState.Empty) {
    submenu(
        Strings.MenuButtons.Questions.Question,
        Strings.MenuButtons.Questions.QuestionDesciption,
        MenuState.Questions.Main
    ) {
        submenu(
            Strings.MenuButtons.Questions.Get,
            Strings.MenuButtons.Questions.Description,
            MenuState.Questions.GetQuestion
        ) {
            submenu(
                Strings.MenuButtons.Questions.MyQuestions.Main,
                Strings.MenuButtons.Questions.MyQuestions.Description,
                MenuState.Questions.GetMyQuestion
            ) {
                button(
                    Strings.MenuButtons.Questions.MyQuestions.ActualQuestions,
                    DialogState.Empty
                ) // /ну видимо хендлер надо тоже или стейт нормальный реализовать
                button(
                    Strings.MenuButtons.Questions.MyQuestions.OldQuestions,
                    DialogState.Empty
                ) // /ну видимо хендлер надо тоже или стейт нормальный реализовать
            }
            button(
                Strings.MenuButtons.Questions.InterestingQuestions,
                DialogState.Empty
            ) // /ну видимо хендлер надо тоже или стейт нормальный реализовать
        }
        button(
            Strings.MenuButtons.Questions.Ask,
            MenuState.Questions.AskQuestion
        ) // /ну видимо хендлер надо тоже или стейт нормальный реализовать
    }
    button(
        Strings.MenuButtons.AddUser,
        MenuState.AddUser
    ) // /ну видимо хендлер надо тоже или стейт нормальный реализовать
    button(
        Strings.MenuButtons.Notifications.Main,
        MenuState.Notifications
    ) // /TODO: в этом стейте Ивану реализовать логику вывода одной кнопки: "приостановить"
    // /если оповещения включены, и "возобновить", если оповещения выключены
    button(Strings.MenuButtons.ChangeAccountInfo, MenuState.ChangeAccountInfo) // //TODO: это я потом реализую
}
