package qna.telegram.flows

import auth.domain.entities.User
import auth.telegram.Strings.OldQuestion.haveNotOldQuestion
import auth.telegram.Strings.OldQuestion.listClosedQuestions
import auth.telegram.Strings.OldQuestion.listOfDefendants
import auth.telegram.queries.SelectRespondent
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.BaseStatefulContext
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
import qna.domain.usecases.NameAndPhoneUseCase
import qna.domain.usecases.SubjectsUseCase

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.oldQuestionFlow() {
    val subjectsUseCase: SubjectsUseCase by inject()
    val nameAndPhoneUseCase: NameAndPhoneUseCase by inject()
    val subjectsPager = pager(id = "sub1") {
        val subjects = subjectsUseCase.invoke(context!!.user.id)
        val paginatedNumbers = subjects.drop(offset).take(limit)
        inlineKeyboard {
            paginatedNumbers.forEach { item ->
                row {
                    dataButton(item.subject, SelectSubject(item.id!!))
                }
            }
            navigationRow(itemCount = subjects.size)
        }
    }
    state<MenuState.OldQuestion> {
        onEnter { chatId ->
            val replyMarkup = subjectsPager.replyMarkup(
                Unit,
                this as BaseStatefulContext<DialogState, User, DialogState, User.Normal>
            )
            if (replyMarkup.keyboard.isNotEmpty()) {
                sendTextMessage(chatId, listClosedQuestions, replyMarkup = replyMarkup)
            } else {
                sendTextMessage(chatId, haveNotOldQuestion)
                state.override { DialogState.Empty }
            }
        }
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            sendTextMessage(
                query.user.id,
                listOfDefendants,
                replyMarkup = inlineKeyboard {
                    nameAndPhoneUseCase.invoke(data.questionId).forEach { item ->
                        row {
                            dataButton(item.key, SelectRespondent(name = item.key, phoneNumber = item.value))
                        }
                    }
                }
            )
            answer(query)
        }
        onDataCallbackQuery(SelectRespondent::class) { (data, query) ->
            sendContact(
                query.user.id,
                phoneNumber = data.phoneNumber,
                firstName = data.name
            )
            answer(query)
            state.override { DialogState.Empty }
        }
    }
}
