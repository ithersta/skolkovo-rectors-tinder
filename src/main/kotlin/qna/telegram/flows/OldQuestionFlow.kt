package qna.telegram.flows

import auth.domain.entities.User
import auth.telegram.Strings.OldQuestion.HaveNotOldQuestion
import auth.telegram.Strings.OldQuestion.ListClosedQuestions
import auth.telegram.Strings.OldQuestion.ListOfDefendants
import auth.telegram.queries.SelectRespondent
import auth.telegram.queries.SelectTopic
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.pager
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendContact
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.usecases.GetNameAndPhoneUseCase
import qna.domain.usecases.GetQuestionByUserIdUseCase

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.oldQuestionFlow() {
    val subjectsUseCase: GetQuestionByUserIdUseCase by inject()
    val nameAndPhoneUseCase: GetNameAndPhoneUseCase by inject()
    val subjectsPager = pager(id = "sub1") {
        val subjects = subjectsUseCase.invoke(context!!.user.id)
        val paginatedSubjects = subjects.drop(offset).take(limit)
        inlineKeyboard {
            paginatedSubjects.forEach { item ->
                row {
                    dataButton(item.subject, SelectTopic(item.id!!))
                }
            }
            navigationRow(itemCount = subjects.size)
        }
    }
    state<MenuState.OldQuestion> {
        onEnter { chatId ->
            val replyMarkup = subjectsPager.replyMarkup(Unit)
            if (replyMarkup.keyboard.isNotEmpty()) {
                sendTextMessage(chatId, ListClosedQuestions, replyMarkup = replyMarkup)
            } else {
                sendTextMessage(chatId, HaveNotOldQuestion)
                state.override { DialogState.Empty }
            }
        }
        onDataCallbackQuery(SelectTopic::class) { (data, query) ->
            sendTextMessage(
                query.user.id,
                ListOfDefendants,
                replyMarkup = inlineKeyboard {
                    nameAndPhoneUseCase.invoke(data.questionId).forEach { item ->
                        row {
                            dataButton(
                                item.name,
                                SelectRespondent(name = item.name, phoneNumber = item.phoneNumber.toString())
                            )
                        }
                    }
                }
            )
            answer(query)
        }
        onDataCallbackQuery(SelectRespondent::class) { (data, query) ->
            sendContact(query.user.id, phoneNumber = data.phoneNumber, firstName = data.name)
            answer(query)
            state.override { DialogState.Empty }
        }
    }
}