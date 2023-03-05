package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.BaseStatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.pager
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import feedback.domain.usecases.CloseQuestionUseCase
import generated.dataButton
import generated.onDataCallbackQuery
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.usecases.*
import qna.telegram.queries.CloseQuestion
import qna.telegram.queries.SeeList
import qna.telegram.queries.SelectRespondent
import qna.telegram.queries.SelectSubject
import qna.telegram.states.ChooseAction
import qna.telegram.strings.ButtonStrings
import qna.telegram.strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.getListOfRespondentNoAnswerFlow() {
    val getQuestionsByUserIdUseCase: GetQuestionsByUserIdUseCase by inject()
    val closeQuestionUseCase: CloseQuestionUseCase by inject()
    val getRespondentsByQuestionIdUseCase: GetRespondentsByQuestionIdUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val addResponseUseCase: AddResponseUseCase by inject()
    // выводится список тем актуальных вопросов пользователя
    // потом выводится 2 кнопки - закрыть вопрос и посмотреть список ответивших
    // выводится список имен ответчиков
    // по порядку все люди, которые согласились ответить
    val subjectPager = pager(id = "subjectsNoAnswer") {
        val subject = getQuestionsByUserIdUseCase(context!!.user.id)
        val pagSubject = subject.drop(offset).take(limit)
        inlineKeyboard {
            pagSubject.forEach { item ->
                row {
                    dataButton(item.subject, SelectSubject(item.authorId, item.id!!))
                }
            }
            navigationRow(itemCount = subject.size)
        }
    }
    val respondentPager = pager(id = "respondentNoAnswer", dataKClass = SeeList::class) {
        val respondent = getRespondentsByQuestionIdUseCase(data.questionId)
        val pagRespondent = respondent.drop(offset).take(limit)
        inlineKeyboard {
            pagRespondent.forEach { item ->
                row {
                    val user = getUserDetailsUseCase(item)
                    dataButton(user!!.name, SelectRespondent(user.id, data.questionId))
                }
            }
            navigationRow(itemCount = respondent.size)
        }
    }
    state<MenuState.GetListOfSubjects> {
        onEnter {
            val replyMarkup =
                subjectPager.replyMarkup(Unit, this as BaseStatefulContext<DialogState, User, DialogState, User.Normal>)
            if (getQuestionsByUserIdUseCase(it.chatId).isEmpty()) {
                sendTextMessage(it, Strings.RespondentsNoAnswer.NoQuestions)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(it, Strings.RespondentsNoAnswer.ListOfSubjects, replyMarkup = replyMarkup)
            }
        }
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            state.override { ChooseAction(data.userId, data.questionId) }
            answer(query)
        }
    }
    state<ChooseAction> {
        onEnter {
            sendTextMessage(
                it,
                Strings.RespondentsNoAnswer.ChooseAction,
                replyMarkup = inlineKeyboard {
                    row {
                        dataButton(
                            ButtonStrings.RespondentNoAnswer.CloseQuestion,
                            CloseQuestion(state.snapshot.userId, state.snapshot.questionId)
                        )
                    }
                    row {
                        dataButton(
                            ButtonStrings.RespondentNoAnswer.SeeList,
                            SeeList(state.snapshot.userId, state.snapshot.questionId)
                        )
                    }
                }
            )
        }
        onDataCallbackQuery(CloseQuestion::class) { (data, query) ->
            closeQuestionUseCase(data.userId, data.questionId)
            sendTextMessage(query.user.id, Strings.RespondentsNoAnswer.CloseQuestionSuccessful)
            state.override { DialogState.Empty }
            answer(query)
        }
        onDataCallbackQuery(SeeList::class) { (data, query) ->
            val replyMarkup =
                respondentPager.replyMarkup(
                    data,
                    this as BaseStatefulContext<DialogState, User, DialogState, User.Normal>
                )
            if (getRespondentsByQuestionIdUseCase(state.snapshot.questionId).isEmpty()) {
                sendTextMessage(query.user.id, Strings.RespondentsNoAnswer.NoRespondent)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(query.user.id, Strings.RespondentsNoAnswer.ListOfRespondents, replyMarkup = replyMarkup)
            }
            answer(query)
        }
    }
    anyState {
        onDataCallbackQuery(SelectRespondent::class) { (data, query) ->
            //тут баг (не знаю, как решить)
            //если человек не нажал да/нет, а нажал команду отменить, то потом участник
            // в списке встречается не 1 раз, а 2, 3 и тд
            //возможно, эта проблема везде, где используется такая функция
            //наверно, нужна проверка, чтобы questionId respondentId полностью не совпадали
            // (то есть, чтобы не было 2-х и более одинаковых строк в бд с questionId respondentId)
            addResponseUseCase(data.questionId, data.respondentId)
            answer(query)
        }
    }
}
