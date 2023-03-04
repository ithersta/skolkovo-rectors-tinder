package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.BaseStatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.pager
import com.ithersta.tgbotapi.pagination.statefulPager
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.send
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
    val getQuestionsByUserIdUseCase: GetQuestionsByUserIdUseCase by inject()
    val getQuestionByIdUseCase: GetQuestionByIdUseCase by inject()
    val getRespondentsByQuestionIdUseCase: GetRespondentsByQuestionIdUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val addResponseUseCase: AddResponseUseCase by inject()
    //выводится список тем актуальных вопросов пользователя
    //потом выводится 2 кнопки - закрыть вопрос и посмотреть список ответивших
    //выводится список имен ответчиков
    //по порядку все люди, которые согласились ответить
    val subPager = pager(id = "subjects") {
        val sub = getQuestionsByUserIdUseCase(context!!.user.id)
        val pagSub = sub.drop(offset).take(limit)
        inlineKeyboard {
            pagSub.forEach { item ->
                row {
                    dataButton(item.toString(), SelectSubject(item.id!!))
                }
            }
            navigationRow(itemCount = sub.size)
        }
    }
    state<MenuState.GetListOfSubjects> {
        onEnter {
            val replyMarkup = subPager.replyMarkup(Unit, this as BaseStatefulContext<DialogState, User, DialogState, User.Normal>)
            if (getQuestionsByUserIdUseCase(it.chatId).isEmpty()) {
                sendTextMessage(it, Strings.RespondentsNoAnswer.NoQuestions)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(it,"текст",  replyMarkup = replyMarkup)
            }
        }
        onDataCallbackQuery(SelectUserArea::class) { (data, query) ->
            state.override { GetListOfSubjects(query.user.id.chatId, data.area) }
            answer(query)
        }
    }
//    state<GetListOfRespondent> {
//        val respondentPager =
//            statefulPager(id = "respondent", onPagerStateChanged = { state.snapshot.copy(pagerState = it) }) {
//                val respondent = getRespondentsByQuestionIdUseCase(state.snapshot.questionId)
//                val paginatedRespondent = respondent.drop(offset).take(limit)
//                inlineKeyboard {
//                    paginatedRespondent.forEach { item ->
//                        row {
//                            getUserDetailsUseCase(item)?.let { dataButton(it.name, SelectRespondent(item)) }
//                        }
//                    }
//                    navigationRow(itemCount = respondent.size)
//                }
//            }
//        onEnter {
//            if (getRespondentsByQuestionIdUseCase(state.snapshot.questionId).isEmpty()) {
//                sendTextMessage(it, Strings.RespondentsNoAnswer.NoRespondent)
//                state.override { DialogState.Empty }
//            } else {
//                with(respondentPager) {
//                    val question = getQuestionByIdUseCase(state.snapshot.questionId)
//                    if (question != null) {
//                        sendOrEditMessage(
//                            it.chatId.toChatId(),
//                            Strings.RespondentsNoAnswer.ListOfRespondents,
//                            state.snapshot.pagerState
//                        )
//                    }
//                }
//            }
//        }
//        onDataCallbackQuery(SelectRespondent::class) { (data, query) ->
//            addResponseUseCase(state.snapshot.questionId, data.respondentId)
//            answer(query)
//        }
//    }
}
