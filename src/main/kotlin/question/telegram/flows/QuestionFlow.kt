package question.telegram.flows

import auth.domain.entities.User
import auth.telegram.queries.AnswerUser
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.usecase.SubjectsUseCase
import qna.domain.usecase.TextsUseCase
import qna.domain.usecase.UserIdUseCase

fun StateMachineBuilder<DialogState, User, UserId>.feedbackFlow() {
    val subjectsByChatId: SubjectsUseCase by inject()
    val textByQuestionId: TextsUseCase by inject()
    val userIdByQuestionId: UserIdUseCase by inject()
    val answerForUser: List<String> = listOf("Да", "Нет")
    role<User.Normal> {
        state<MenuState.CurrentIssues> {
            onEnter {
//                todo: пагинация
                sendTextMessage(
                    it.toChatId(),
                    "Список вопросов по вашей сфере, нажмите на тему что бы посмотреть в чем суть вопроса.",
                    replyMarkup = inlineKeyboard {
                        subjectsByChatId.invoke(it.chatId).forEach {
                            row {
                                dataButton(it.value, SelectSubject(it.key))
                            }
                        }
                    }
                )
            }
            onDataCallbackQuery(SelectSubject::class) { (data, query) ->
                sendTextMessage(
                    query.user.id,
                    textByQuestionId.invoke(data.questionId),
                    replyMarkup = inlineKeyboard {
                        answerForUser.forEach {
                            row {
                                dataButton(it, AnswerUser(data.questionId, it))
                            }
                        }
                    }
                )
                answer(query)
            }
            onDataCallbackQuery(AnswerUser::class) { (data, query) ->
                if (data.answer.equals("Нет")) {
                    sendTextMessage(
                        query.user.id, "Хорошо"
                    )
                } else { // если да то отправить сообщение хозяину вопроса
                    sendTextMessage(
                        userIdByQuestionId.invoke(data.questionId).toChatId(),
                        "text"
                    )

                    sendTextMessage(
                        query.user.id, "Владелец вопроса свяжется с вами"
                    )
                }
                state.override { DialogState.Empty }
                answer(query)
            }
        }
    }
}
