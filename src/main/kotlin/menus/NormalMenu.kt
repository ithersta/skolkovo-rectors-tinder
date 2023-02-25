package menus

import auth.domain.entities.User
import auth.telegram.Strings
import changeAccountInfo.Strings.namesToQueries
import changeAccountInfo.domain.usecases.GetUserDetailsByIdUseCase
import com.ithersta.tgbotapi.fsm.StatefulContext
import com.ithersta.tgbotapi.menu.builders.MenuBuilder
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.menu
import menus.states.MenuState
import mute.domain.usecases.ContainsByIdMuteSettingsUseCase
import mute.telegram.queries.OnOffMuteQuery
import org.koin.core.context.GlobalContext
import qna.telegram.strings.Strings.accountInfo


val normalMenu = menu<User.Normal>(Strings.RoleMenu.Normal, DialogState.Empty) {
    extracted()
}
private val getUserDetailsByIdUseCase: GetUserDetailsByIdUseCase by GlobalContext.get().inject()
private val containsByIdMuteSettingsUseCase: ContainsByIdMuteSettingsUseCase by GlobalContext.get().inject()
fun <S : User> MenuBuilder<DialogState, User, S>.extracted() {
    submenu(
        Strings.MenuButtons.Questions.Question,
        Strings.MenuButtons.Questions.QuestionDescription,
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
                button( // TODO
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
        )
    }
    button(
        Strings.MenuButtons.Notifications.Main
    ) {
        sendMuteRequest(it)
    }
   button(
        Strings.MenuButtons.ChangeAccountInfo
    ){
       sendFieldsToChange(it)
   }
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

private suspend fun <S : User> StatefulContext<DialogState, User, *, S>.sendFieldsToChange(message: CommonMessage<TextContent>) {
    val user= getUserDetailsByIdUseCase(message.chat.id.chatId)!!
    sendTextMessage(
        message.chat,
        accountInfo(user.name,user.city, user.job, user.organization, user.activityDescription),
        replyMarkup = ReplyKeyboardRemove()
    )
    sendTextMessage(
        message.chat,
        changeAccountInfo.Strings.ChooseFieldToChange,
        replyMarkup = inlineKeyboard {
            val names= namesToQueries.keys
            names.chunked(2){
                row{
                    it.forEach {
                        dataButton(it, namesToQueries.get(it)!!)
                    }
                }
            }
        }
    )
}
