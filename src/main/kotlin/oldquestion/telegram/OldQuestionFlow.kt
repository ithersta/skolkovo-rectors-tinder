package oldquestion.telegram

import auth.domain.entities.User
import auth.telegram.Strings.OldQuestion.haveNotOldQuestion
import auth.telegram.Strings.OldQuestion.listClosedQuestions
import auth.telegram.Strings.OldQuestion.listOfDefendants
import auth.telegram.queries.SelectRespondent
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.statefulPager
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
import oldquestion.domain.usecase.NameAndPhoneUseCase
import oldquestion.domain.usecase.SubjectsUseCase
import org.koin.core.component.inject

fun StateMachineBuilder<DialogState, User, UserId>.oldQuestionFlow() {
    val subjectsUseCase: SubjectsUseCase by inject()
    val nameAndPhoneUseCase: NameAndPhoneUseCase by inject()
    role<User.Normal> {
        state<MenuState.OldQuestion> {
            val subjects = subjectsUseCase.invoke(567538391).toList() // replace  digit to chatId.
            val subjectsPager =
                statefulPager(id = "subjects", onPagerStateChanged = { state.snapshot.copy(pagerState = it) }) {
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
                if (subjects.isNotEmpty()) {
                    with(subjectsPager) { sendOrEditMessage(chatId, listClosedQuestions, state.snapshot.pagerState) }
                } else {
                    sendTextMessage(chatId, haveNotOldQuestion)
                    state.override { DialogState.Empty }
                }
            }
            onDataCallbackQuery(SelectSubject::class) { (data, query) ->
                sendTextMessage(query.user.id, listOfDefendants,
                    replyMarkup = inlineKeyboard {
                        nameAndPhoneUseCase.invoke(data.questionId).forEach { item ->
                            row {
                                dataButton(item.key, SelectRespondent(name = item.key, phoneNumber = item.value))
                            }
                        }
                    })
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
}

