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
import qna.domain.usecases.GetQuestionByIdUseCase
import qna.domain.usecases.GetSubjectsByAreaUseCase
import qna.domain.usecases.GetUserDetailsUseCase
import qna.telegram.states.GetListOfSubjects
import qna.telegram.queries.SelectSubject
import qna.telegram.queries.SelectUserArea
import qna.telegram.strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.getListOfRespondentNoAnswerFlow() {
    //Список сфер
    //Далее список всех тем вопросов по сферам (пагинация)
    //Потом при нажатии на тему вопроса список всех пользователей(пока что имён),
    //которые хотят ответить на вопрос(при этом они не приняты/отклонены)(пагинация)
    //При нажатии на имя пользователя выводить ?? (сообщение с его инфой из профиля и кнопки да/нет ???)
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val getSubjectsByAreaUseCase: GetSubjectsByAreaUseCase by inject()
    val getQuestionByIdUseCase: GetQuestionByIdUseCase by inject()
    state<MenuState.GetListOfRespondents> {
        //удалять клаву тут
        onEnter {
            sendTextMessage(
                it.chatId.toChatId(),
                Strings.RespondentsNoAnswer.ListOfAreas,
                replyMarkup = inlineKeyboard {
                    //изменить на получение списка всех областей, по которым задавались вопросы пользователя по его id
                    getUserDetailsUseCase(it.chatId)?.areas?.forEach { area ->
                        row {
                            dataButton(auth.telegram.Strings.questionAreaToString.getValue(area), SelectUserArea(area))
                        }
                    }
                }
            )
        }
        onDataCallbackQuery(SelectUserArea::class) { (data, query) ->
            state.override { GetListOfSubjects(query.user.id.chatId, data.area.ordinal) }
            answer(query)
        }
    }
    state<GetListOfSubjects> {
        val subjectsPager = statefulPager(
            id = "subjects",
            onPagerStateChanged = { state.snapshot.copy(pagerState = it) }) {
            val subjects = getSubjectsByAreaUseCase(state.snapshot.userId, state.snapshot.area).toList()
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
        onEnter {
            with(subjectsPager) {
                sendOrEditMessage(
                    it.chatId.toChatId(),
                    Strings.RespondentsNoAnswer.ListOfSubjects, state.snapshot.pagerState
                )
            }
        }
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            val question = getQuestionByIdUseCase(data.questionId)
            if (question != null) {
                sendTextMessage(
                    query.user.id,
                    Strings.RespondentsNoAnswer.listOfUsers(question.subject),
                    replyMarkup = inlineKeyboard {
                        //тут пагинация
                    })
            }
            //тут переходить на список всех, кто хочет ответить на вопрос(при нажатии на тему вопроса)(пагинация)
            answer(query)
        }
    }
}
