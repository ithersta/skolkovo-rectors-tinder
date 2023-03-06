package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.BaseStatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import com.ithersta.tgbotapi.pagination.replyMarkup
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
import qna.telegram.strings.ButtonStrings
import qna.telegram.strings.Strings

lateinit var subjectPager: InlineKeyboardPager<Unit, DialogState, User, User.Normal>
lateinit var respondentPager: InlineKeyboardPager<SeeList, DialogState, User, User.Normal>

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.getListOfRespondentNoAnswerFlow() {
    val getQuestionsByUserIdUseCase: GetQuestionsByUserIdUseCase by inject()
    val closeQuestionUseCase: CloseQuestionUseCase by inject()
    val getRespondentsByQuestionIdUseCase: GetRespondentsByQuestionIdUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val addResponseUseCase: AddResponseUseCase by inject()

    subjectPager = pager(id = "subjectsNoAnswer") {
        val subject = getQuestionsByUserIdUseCase(context!!.user.id, offset, limit)
        inlineKeyboard {
            subject.forEach { item ->
                row {
                    dataButton(item.subject, SelectSubject(item.authorId, item.id!!))
                }
            }
            navigationRow(itemCount = subject.size)
        }
    }
    respondentPager = pager(id = "respondentNoAnswer", dataKClass = SeeList::class) {
        val respondent = getRespondentsByQuestionIdUseCase(data.questionId, offset, limit)
        inlineKeyboard {
            respondent.forEach { item ->
                val user = getUserDetailsUseCase(item)
                row {
                    dataButton(user!!.name, SelectRespondent(user.id, data.questionId))
                }
            }
            navigationRow(itemCount = respondent.size)
        }
    }
    state<MenuState.Questions.GetListOfSubjects> {
        onEnter {
            val replyMarkup = subjectPager.replyMarkup
            if (replyMarkup.keyboard.isEmpty()) {
                sendTextMessage(it, Strings.RespondentsNoAnswer.NoQuestions)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(it, Strings.RespondentsNoAnswer.ListOfSubjects, replyMarkup = replyMarkup)
                state.overrideQuietly { DialogState.Empty }
            }
        }
    }
    anyState {
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            sendTextMessage(
                query.user.id,
                Strings.RespondentsNoAnswer.ChooseAction,
                replyMarkup = inlineKeyboard {
                    row {
                        dataButton(
                            ButtonStrings.RespondentNoAnswer.CloseQuestion,
                            CloseQuestion(data.userId, data.questionId)
                        )
                    }
                    row {
                        dataButton(
                            ButtonStrings.RespondentNoAnswer.SeeList,
                            SeeList(data.userId, data.questionId)
                        )
                    }
                }
            )
            answer(query)
        }
        onDataCallbackQuery(CloseQuestion::class) { (data, query) ->
            closeQuestionUseCase(data.userId, data.questionId)
            sendTextMessage(query.user.id, Strings.RespondentsNoAnswer.CloseQuestionSuccessful)
            state.override { DialogState.Empty }
            answer(query)
        }
        onDataCallbackQuery(SeeList::class) { (data, query) ->
            val replyMarkup = respondentPager.replyMarkup(
                data,
                this as BaseStatefulContext<DialogState, User, DialogState, User.Normal>
            )
            if (replyMarkup.keyboard.isEmpty()) {
                sendTextMessage(query.user.id, Strings.RespondentsNoAnswer.NoRespondent)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(
                    query.user.id,
                    Strings.RespondentsNoAnswer.ListOfRespondents,
                    replyMarkup = replyMarkup
                )
            }
            answer(query)
        }
        onDataCallbackQuery(SelectRespondent::class) { (data, query) ->
            addResponseUseCase(data.questionId, data.respondentId)
            answer(query)
        }
    }
}
