package question.telegram.flows

import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.Strings.TargetArea.AnswerToPersonWhoAskedQuestion
import auth.telegram.Strings.TargetArea.Good
import auth.telegram.Strings.TargetArea.ListQuestion
import auth.telegram.Strings.TargetArea.No
import auth.telegram.Strings.TargetArea.ReplyToRespondent
import auth.telegram.Strings.TargetArea.Yes
import auth.telegram.Strings.TargetArea.buildQuestionByQuestionText
import auth.telegram.Strings.TargetArea.listSpheres
import auth.telegram.queries.AnswerUser
import auth.telegram.queries.SelectArea
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.PagerState
import com.ithersta.tgbotapi.pagination.statefulPager
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
    val getAreasUseCase: GetAreasUseCase by inject()
    val answerForUser: List<String> = listOf(Yes, No)
    role<User.Normal> {
        state<MenuState.CurrentIssues> {
             onEnter {
                 sendTextMessage(
                     it.chatId.toChatId(),
                     listSpheres,
                     replyMarkup = inlineKeyboard {
                         getAreasUseCase.invoke(it.chatId).forEach { area ->
                             val areaToString = Strings.questionAreaToString[area]
                             row {
                                 dataButton(areaToString!!, SelectArea(area))
                             }
                         }
                     }
                 )
             }
            onDataCallbackQuery(SelectArea::class) { (data, query) ->
                state.override { MenuState.NextStep(data.area, PagerState()) }
            }
        }
        state<MenuState.NextStep> {
            val subjectsPager = statefulPager(
                id = "subjects",
                onPagerStateChanged = { state.snapshot.copy(pagerState = it) }
            ) {
                val subjects = subjectsByChatId.invoke(567538391).toList() // todo: digit to chatId
                val paginatedNumbers = subjects.drop(offset).take(limit)
                inlineKeyboard {
                    paginatedNumbers.forEach { item ->
                        row {
                            dataButton(item.second, SelectSubject(item.first))
                        }
                    }
                    navigationRow(itemCount = subjects.size)
                }
            }
            onEnter { chatId ->
                with(subjectsPager) { sendOrEditMessage(chatId, ListQuestion, state.snapshot.pagerState!!) }
            }
            onDataCallbackQuery(SelectSubject::class) { (data, query) ->
                sendTextMessage(query.user.id,
                    buildQuestionByQuestionText(textByQuestionId.invoke(data.questionId)),
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
                    sendTextMessage(query.user.id, Good)
                } else {
                    val userId = userIdByQuestionId.invoke(data.questionId)
                    val chatId = userId.toChatId()
                    sendTextMessage(chatId, AnswerToPersonWhoAskedQuestion)
                    sendContact(
                        chatId, phoneNumber = getPhoneNumberUseCase.invoke(userId),
                        firstName = getFirstNameUseCase.invoke(userId)
                    )
                    sendTextMessage(query.user.id, ReplyToRespondent)
                }
                state.override { DialogState.Empty }
                answer(query)
            }
        }
    }
}
