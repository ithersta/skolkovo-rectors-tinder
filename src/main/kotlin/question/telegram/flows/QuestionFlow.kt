package question.telegram.flows

import auth.domain.entities.User
import auth.telegram.Strings.TargetArea.AnswerToPersonWhoAskedQuestion
import auth.telegram.Strings.TargetArea.Good
import auth.telegram.Strings.TargetArea.ListQuestion
import auth.telegram.Strings.TargetArea.No
import auth.telegram.Strings.TargetArea.ReplyToRespondent
import auth.telegram.Strings.TargetArea.Yes
import auth.telegram.queries.AnswerUser
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendContact
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.usecase.*

fun StateMachineBuilder<DialogState, User, UserId>.feedbackFlow() {
    val subjectsByChatId: SubjectsUseCase by inject()
    val textByQuestionId: TextsUseCase by inject()
    val userIdByQuestionId: UserIdUseCase by inject()
    val getPhoneNumberUseCase: GetPhoneNumberUseCase by inject()
    val getFirstNameUseCase: GetFirstNameUseCase by inject()
    val answerForUser: List<String> = listOf(Yes, No)
    role<User.Normal> {
        state<MenuState.CurrentIssues> {
            onEnter {
//                todo: пагинация
                sendTextMessage(
                    it.toChatId(),
                    ListQuestion,
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
                if (data.answer == No) {
                    sendTextMessage(
                        query.user.id, Good
                    )
                } else {
                    val userId = userIdByQuestionId.invoke(data.questionId)
                    val chatId = userId.toChatId()
                    sendTextMessage(
                        chatId,
                        AnswerToPersonWhoAskedQuestion
                    )
                    sendContact(
                        chatId,
                        phoneNumber = getPhoneNumberUseCase.invoke(userId),
                        firstName = getFirstNameUseCase.invoke(userId)
                    )
                    sendTextMessage(
                        query.user.id, ReplyToRespondent
                    )
                }
                state.override { DialogState.Empty }
                answer(query)
            }
        }
    }
}
