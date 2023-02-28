package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.statefulPager
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
import qna.domain.usecases.*
import qna.telegram.queries.SelectRespondent
import qna.telegram.queries.SelectSubject
import qna.telegram.queries.SelectUserArea
import qna.telegram.states.GetListOfRespondent
import qna.telegram.states.GetListOfSubjects
import qna.telegram.strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.getListOfRespondentNoAnswerFlow() {
    val getSubjectsByAreaUseCase: GetSubjectsByAreaUseCase by inject()
    val getQuestionByIdUseCase: GetQuestionByIdUseCase by inject()
    val getQuestionAreasByUserId: GetQuestionAreasByUserId by inject()
    val getRespondentsByQuestionIdUseCase: GetRespondentsByQuestionIdUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    state<MenuState.GetListOfRespondents> {
        onEnter {
            if (getQuestionAreasByUserId(it.chatId).isEmpty()) {
                sendTextMessage(it, Strings.RespondentsNoAnswer.NoQuestions)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(
                    it,
                    Strings.RespondentsNoAnswer.ListOfAreas,
                    replyMarkup = inlineKeyboard {
                        getQuestionAreasByUserId(it.chatId).forEach { area ->
                            row {
                                dataButton(
                                    auth.telegram.Strings.questionAreaToString.getValue(area),
                                    SelectUserArea(area)
                                )
                            }
                        }
                    }
                )
            }
        }
        onDataCallbackQuery(SelectUserArea::class) { (data, query) ->
            state.override { GetListOfSubjects(query.user.id.chatId, data.area.ordinal) }
            answer(query)
        }
    }
    state<GetListOfSubjects> {
        val subjectsPager =
            statefulPager(id = "subjects", onPagerStateChanged = { state.snapshot.copy(pagerState = it) }) {
                val subjects = getSubjectsByAreaUseCase(state.snapshot.userId, state.snapshot.area).toList()
                val paginatedSubjects = subjects.drop(offset).take(limit)
                inlineKeyboard {
                    paginatedSubjects.forEach { item ->
                        row {
                            dataButton(item.second, SelectSubject(item.first))
                        }
                    }
                    navigationRow(itemCount = subjects.size)
                }
            }
        onEnter {
            with(subjectsPager) {
                sendOrEditMessage(
                    it.chatId.toChatId(),
                    Strings.RespondentsNoAnswer.ListOfSubjects,
                    state.snapshot.pagerState
                )
            }
        }
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            state.override { GetListOfRespondent(query.user.id.chatId, data.questionId) }
            answer(query)
        }
    }
    state<GetListOfRespondent> {
        val respondentPager =
            statefulPager(id = "respondent", onPagerStateChanged = { state.snapshot.copy(pagerState = it) }) {
                val respondent = getRespondentsByQuestionIdUseCase(state.snapshot.questionId)
                val paginatedRespondent = respondent.drop(offset).take(limit)
                inlineKeyboard {
                    paginatedRespondent.forEach { item ->
                        row {
                            getUserDetailsUseCase(item)?.let { dataButton(it.name, SelectRespondent(item)) }
                        }
                    }
                    navigationRow(itemCount = respondent.size)
                }
            }
        onEnter {
            if (getRespondentsByQuestionIdUseCase(state.snapshot.questionId).isEmpty()) {
                sendTextMessage(it, Strings.RespondentsNoAnswer.NoRespondent)
                state.override { DialogState.Empty }
            } else {
                with(respondentPager) {
                    val question = getQuestionByIdUseCase(state.snapshot.questionId)
                    if (question != null) {
                        sendOrEditMessage(
                            it.chatId.toChatId(),
                            Strings.RespondentsNoAnswer.ListOfRespondents,
                            state.snapshot.pagerState
                        )
                    }
                }
            }
        }
        onDataCallbackQuery(SelectRespondent::class) { (data, query) ->
            // использовать функцию
            answer(query)
        }
    }
}
