package qna.flows

import auth.domain.entities.User
import auth.domain.usecases.GetUsersByAreaUseCase
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import qna.domain.entities.QuestionArea
import qna.states.SendingQuestionToCommunity
import qna.strings.ButtonStrings
import qna.strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.contactWithUserFlow() {
    val getUsersByAreaUseCase: GetUsersByAreaUseCase by inject()
    // удалю позже
    state<SendingQuestionToCommunity> {
        onEnter {
            sendTextMessage(
                it,
                "Ваш вопрос успешно отправлен!"
            )
        }
        onText { message ->
            // TODO: не отправляется с inline, но отправляется с reply
            coroutineScope.launch {
                val listOfValidUsers: List<Long> =
                    getUsersByAreaUseCase(
                        QuestionArea.Education,
                        userId = message.chat.id.chatId
                    ) // брать areas, которую задал пользователь
                listOfValidUsers.forEach {
                    println(it.toChatId())
                    runCatching {
                        sendTextMessage(
                            it.toChatId(),
                            Strings.ToAnswerUser.message("тема", "вопрос"),
                            replyMarkup = inlineKeyboard {
                                row {
                                    dataButton(ButtonStrings.Option.Yes, "Да")
                                }
                                row {
                                    dataButton(ButtonStrings.Option.No, "Нет")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
//        onText(ButtonStrings.Yes){
//            sendTextMessage(it.chat, Strings.SentAgreement)
//            //отправлять согласие владельцу вопроса
//            //переход на другое состояние
//            state.override { CommunicateWithUser }
//        }
//        onText(ButtonStrings.No){
//            //удалять сообщение о предложении ответить на вопрос
//            //остаемся в состоянии, которое было до перехода в это состояние
//        }
//    }
//    state<CommunicateWithUser> {
//        onEnter {
//            sendTextMessage(
//                it,
//                Strings.ToAskUser.message("profile"), //поменять на переменную профиля участника (берем из бд)
//                replyMarkup = replyKeyboard(
//                    resizeKeyboard = true,
//                    oneTimeKeyboard = true
//                ) {
//                    row {
//                        simpleButton(ButtonStrings.Yes)
//                        simpleButton(ButtonStrings.No)
//                    }
//                }
//            )
//        }
//        onText(ButtonStrings.Yes) {
//            sendTextMessage(it.chat, Strings.WriteToCompanion)
//            sendTextMessage(it.chat, Strings.CopyQuestion)
//            sendTextMessage(it.chat, "question") //тут отправка вопроса (берем из бд)
//            //отправлять инфу о согласии тому, кто вызывался ответить на вопрос
//            //думаю, что нужно переходить в др состояние
//        }
//        onText(ButtonStrings.No) {
//            //отправлять инфу о несогласии тому, кто вызывался ответить на вопрос
//            //думаю, что нужно переходить в др состояние
//        }
//    }
}
