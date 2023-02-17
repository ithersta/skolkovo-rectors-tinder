package question.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.usecase.GetUserIdUserCase
import qna.domain.usecase.QuestionsUseCase

fun StateMachineBuilder<DialogState, User, UserId>.feedbackFlow() {
    val questions: QuestionsUseCase by inject()
    val getUserId: GetUserIdUserCase by inject()
    role<User.Normal> {
        state<MenuState.CurrentIssues> {
            onEnter {
                val chatId = it.toChatId()
                val text: String = ""
                for (area in getUserId(chatId.chatId)) {
                    println(area)
                    text.plus(questions.invoke(userSubject = area.toString()))
                }
                println(text)
                sendTextMessage(
                    it.toChatId(),
                    text
                )
            }
        }
    }
}
