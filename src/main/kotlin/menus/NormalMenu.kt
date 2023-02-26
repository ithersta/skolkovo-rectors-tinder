package menus

import auth.domain.entities.User
import auth.telegram.Strings
import com.ithersta.tgbotapi.fsm.StatefulContext
import com.ithersta.tgbotapi.menu.builders.MenuBuilder
import com.ithersta.tgbotapi.pagination.PagerState
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.menu
import menus.states.MenuState
import mute.domain.usecases.ContainsByIdMuteSettingsUseCase
import mute.telegram.queries.OnOffMuteQuery
import org.koin.core.context.GlobalContext

val normalMenu = menu<User.Normal>(Strings.RoleMenu.Normal, DialogState.Empty) {
    extracted()
}
private val containsByIdMuteSettingsUseCase: ContainsByIdMuteSettingsUseCase by GlobalContext.get().inject()
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
                    MenuState.OldQuestion(PagerState())
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
        Strings.MenuButtons.Notifications.Main
    ) {
        sendMuteRequest(it)
    }
    // /TODO: в этом стейте Ивану реализовать логику вывода одной кнопки: "приостановить"
    // /если оповещения включены, и "возобновить", если оповещения выключены
    button(Strings.MenuButtons.ChangeAccountInfo, MenuState.ChangeAccountInfo) // //TODO: это я потом реализую
    button(
        Strings.MenuButtons.Events,
        MenuState.Events
    )
}

private suspend fun <S : User> StatefulContext<DialogState, User, *, S>.sendMuteRequest(message: CommonMessage<TextContent>) {
    sendTextMessage(
        message.chat,
        Strings.MenuButtons.Notifications.Description,
        replyMarkup = inlineKeyboard {
            row {
                if (containsByIdMuteSettingsUseCase(message.chat.id.chatId)) {
                    dataButton(Strings.MenuButtons.Notifications.On, OnOffMuteQuery(true))
                } else {
                    dataButton(Strings.MenuButtons.Notifications.Off, OnOffMuteQuery(false))
                }
            }
        }
    )
}
