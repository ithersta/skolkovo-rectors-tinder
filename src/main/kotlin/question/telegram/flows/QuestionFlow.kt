package question.telegram.flows

import auth.domain.entities.User
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.usecase.UserAreasUseCase


fun StateMachineBuilder<DialogState, User, UserId>.feedbackFlow() {
    val user: UserAreasUseCase by inject()
    role<User.Normal> {
        state<MenuState.CurrentIssues> {
            onEnter {
                sendTextMessage(
                    it.toChatId(),
                    "Вопросы на вашим сферам",
                    replyMarkup = inlineKeyboard {
                        user.invoke(it.chatId).forEach {
                            row {
                                dataButton(it, SelectSubject(it))
                            }
                        }
                    }
                )
            }
        }
    }
}
