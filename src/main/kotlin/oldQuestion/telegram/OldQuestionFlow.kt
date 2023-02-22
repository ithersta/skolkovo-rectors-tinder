package oldQuestion.telegram

import auth.domain.entities.User
import auth.telegram.Strings.OldQuestion.listClosedQuestions
import auth.telegram.Strings.OldQuestion.listOfDefendants
import auth.telegram.queries.SelectRespondent
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
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
import oldQuestion.domain.usecase.NameAndPhoneUseCase
import oldQuestion.domain.usecase.SubjectsUseCase
import org.koin.core.component.inject

fun StateMachineBuilder<DialogState, User, UserId>.oldQuestionFlow() {
    val subjectsUseCase: SubjectsUseCase by inject()
    val nameAndPhoneUseCase: NameAndPhoneUseCase by inject()
    role<User.Normal> {
        state<MenuState.OldQuestion> {
            //  todo: пагинация.
            onEnter {
                val chatId = it.chatId
                sendTextMessage(
                    chatId.toChatId(),
                    text = listClosedQuestions,
                    replyMarkup = inlineKeyboard {
                        subjectsUseCase.invoke(chatId).forEach { item ->
                            row {
                                dataButton(item.value, SelectSubject(item.key))
                            }
                        }
                    }
                )
            }
            onDataCallbackQuery(SelectSubject::class) { (data, query) ->
                sendTextMessage(
                    query.user.id,
                    text = listOfDefendants,
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
            }
        }
    }
}