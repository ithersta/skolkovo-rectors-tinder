package menus

import auth.domain.entities.User
import auth.telegram.Strings
import com.ithersta.tgbotapi.menu.builders.MenuBuilder
import common.telegram.DialogState
import common.telegram.strings.CommonStrings
import generated.menu
import menus.states.MenuState
import menus.strings.MenuStrings
import qna.telegram.flows.sendListOfRespondentNoAnswer
import qna.telegram.flows.sendOldQuestionsPager
import qna.telegram.flows.sendQuestionDigestAreas

val normalMenu = menu<User.Normal>(Strings.RoleMenu.Normal, DialogState.Empty) {
    menu()
}

fun <S : User.Normal> MenuBuilder<DialogState, User, S>.menu() {
    submenu(
        MenuStrings.Questions.Question,
        MenuStrings.Questions.QuestionDescription,
        MenuState.Questions.Main
    ) {
        button(
            MenuStrings.Questions.Ask,
            MenuState.Questions.AskQuestion
        )
        submenu(
            MenuStrings.Questions.Get,
            MenuStrings.Questions.Description,
            MenuState.Questions.GetQuestion
        ) {
            submenu(
                MenuStrings.Questions.MyQuestions.Main,
                MenuStrings.Questions.MyQuestions.Description,
                MenuState.Questions.GetMyQuestion
            ) {
                button(MenuStrings.Questions.MyQuestions.ActualQuestions) {
                    sendListOfRespondentNoAnswer(it.chat.id)
                }
                button(MenuStrings.Questions.MyQuestions.OldQuestions) {
                    sendOldQuestionsPager(it.chat.id)
                }
                backButton(CommonStrings.Button.Back)
            }
            button(MenuStrings.Questions.InterestingQuestions) {
                sendQuestionDigestAreas(it.chat.id)
            }
            backButton(CommonStrings.Button.Back)
        }
        backButton(CommonStrings.Button.Back)
    }
    button(MenuStrings.AccountInfo.Main, MenuState.AccountInfoState)

    button(
        MenuStrings.Events,
        MenuState.Events
    )
}
