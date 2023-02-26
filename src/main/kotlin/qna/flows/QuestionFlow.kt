package qna.flows

import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.queries.AnswerUser
import auth.telegram.queries.SelectArea
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.PagerState
import com.ithersta.tgbotapi.pagination.statefulPager
import common.telegram.CommonStrings.Button.No
import common.telegram.CommonStrings.Button.Yes
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
import qna.domain.usecases.*
import qna.strings.Strings.TargetArea.AnswerToPersonWhoAskedQuestion
import qna.strings.Strings.TargetArea.Good
import qna.strings.Strings.TargetArea.ListQuestion
import qna.strings.Strings.TargetArea.ReplyToRespondent
import qna.strings.Strings.TargetArea.buildQuestionByQuestionText
import qna.strings.Strings.TargetArea.listSpheres

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.feedbackFlow() {
    val subjectsByChatId: SubjectsUseCase by inject()
    val getPhoneNumberUseCase: GetPhoneNumberUseCase by inject()
    val getQuestionByIdUseCase: GetQuestionByIdUseCase by inject()
    val getFirstNameUseCase: GetFirstNameUseCase by inject()
    val getAreasUseCase: GetAreasUseCase by inject()
    val answerForUser: List<String> = listOf(Yes, No)
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
            state.override { MenuState.NextStep(query.user.id.chatId, data.area.ordinal, PagerState()) }
            answer(query)
        }
    }
    state<MenuState.NextStep> {
        val subjectsPager =
            statefulPager(id = "subjects", onPagerStateChanged = { state.snapshot.copy(pagerState = it) }) {
                val subjects = subjectsByChatId.invoke(state.snapshot.userId, state.snapshot.areaIndex).toList()
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
            with(subjectsPager) { sendOrEditMessage(chatId, ListQuestion, state.snapshot.pagerState) }
        }
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            sendTextMessage(
                query.user.id,
                buildQuestionByQuestionText(getQuestionByIdUseCase.invoke(data.questionId)!!.text),
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
                val userId = getQuestionByIdUseCase.invoke(data.questionId)!!.id
                val chatId = userId!!.toChatId()
                sendTextMessage(chatId, AnswerToPersonWhoAskedQuestion)
                sendContact(
                    chatId,
                    phoneNumber = getPhoneNumberUseCase.invoke(userId),
                    firstName = getFirstNameUseCase.invoke(userId)
                )
                sendTextMessage(query.user.id, ReplyToRespondent)
            }
            state.override { DialogState.Empty }
            answer(query)
        }
    }
}
// todo: тут я и Вика не знаем как использовать данную функцию.
/*
suspend fun StatefulContext<DialogState, User, MenuState.AnswerUser, User.Normal>.sendQMesssage(
    chatId: ChatId,
    question: Question
) = sendTextMessage(
    chatId,
    qna.strings.Strings.ToAnswerUser.message(question.subject, question.text),
    replyMarkup = inlineKeyboard {
        row {
            checkNotNull(question.id)
            dataButton(
                CommonStrings.Button.Yes,
                AcceptQuestionQuery(question.id)
            )
        }
        row {
            dataButton(
                CommonStrings.Button.No,
                DeclineQuestionQuery
            )
        }
    }
)
*/
