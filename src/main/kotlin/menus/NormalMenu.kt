package menus

import auth.domain.entities.User
import auth.telegram.Strings
import com.ithersta.tgbotapi.menu.builders.MenuBuilder
import common.telegram.DialogState
import generated.menu
import menus.states.MenuState

val normalMenu = menu<User.Normal>(Strings.RoleMenu.Normal, DialogState.Empty) {
    extracted()
}

fun <S : User> MenuBuilder<DialogState, User, S>.extracted() {
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
        ) // задать вопрос (в процессе разработки)
        // надо удалять replyKeabord, если это возможно
    }
    button(
        Strings.MenuButtons.Notifications.Main,
        MenuState.Notifications
    ) // /TODO: в этом стейте Ивану реализовать логику вывода одной кнопки: "приостановить"
    // /если оповещения включены, и "возобновить", если оповещения выключены
    button(Strings.MenuButtons.ChangeAccountInfo, MenuState.ChangeAccountInfo) // //TODO: это я потом реализую
    button(
        Strings.MenuButtons.Events,
        MenuState.Events
    ) // /TODO: в этом стейте Глебу реализовать логику вывода мероприятий
}
