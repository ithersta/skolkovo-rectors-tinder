package menus

import auth.domain.entities.User
import auth.telegram.Strings
import com.ithersta.tgbotapi.menu.builders.menu
import states.DialogState
import states.MenuState

val normalMenu = menu<DialogState, User, User.Unauthenticated>(Strings.RoleMenu.Normal, DialogState.Empty) {
    submenu(
        Strings.MenuButtons.Questions.Question,
        Strings.MenuButtons.Questions.QuestionDesciption,
        MenuState.Questions
    ) {
        submenu(
            Strings.MenuButtons.Questions.Get,
            Strings.MenuButtons.Questions.Description,
            MenuState.Questions
        ) {
            submenu(
                Strings.MenuButtons.Questions.MyQuestions.Main,
                Strings.MenuButtons.Questions.MyQuestions.Description,
                MenuState.Questions
            ) {
                button(
                    Strings.MenuButtons.Questions.MyQuestions.ActualQuestions,
                    DialogState.Empty
                ) // /ну видимо хендлер надо тоже
                button(
                    Strings.MenuButtons.Questions.MyQuestions.OldQuestions,
                    DialogState.Empty
                ) // /ну видимо хендлер надо тоже
            }
            button(Strings.MenuButtons.Questions.InterestingQuestions, DialogState.Empty) // /ну видимо хендлер надо тоже
        }
        button(Strings.MenuButtons.Questions.Ask, DialogState.Empty) // /ну видимо хендлер надо тоже
    }
}
