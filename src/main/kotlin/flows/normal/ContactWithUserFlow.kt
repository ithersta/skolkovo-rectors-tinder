package flows.normal

import auth.domain.entities.User
import auth.domain.repository.UserAreasRepository
import auth.domain.repository.UserRepository
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import states.*
import strings.ButtonStrings
import strings.Strings

fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.contactWithUserFlow() { // User.Normal

    val userRepository: UserRepository by inject()
    val userAreasRepository: UserAreasRepository by inject()
    state<AnswerToQuestion> {
        // посмотреть инфу из preaccelerator
//        onEnter{
//            sendTextMessage(it, Strings.ToAnswerUser.message("que"), //поменять на переменную текста вопроса (берем из бд)
//                replyMarkup = replyKeyboard (
//                    resizeKeyboard = true,
//                    oneTimeKeyboard = true
//            ) {
//                    row{
//                        simpleButton(ButtonStrings.Yes)
//                        simpleButton(ButtonStrings.No)
//                    }
//                }
//            )
//        }
        onEnter {
            sendTextMessage(it, "Задайте свой вопрос") // сделала для тестирования
            // тут для полноценной проверки нужно задать сферу вопроса и все остальное... HELP
        }
        onText { message ->
            // тут добавляем проверку на area question и user question(их может быть 1 и более) что и активный пользователь(если нет таблицы mutesettings) и отправляем всем, кто подходит(кроме того, кто задал вопрос)
            coroutineScope.launch {
                userAreasRepository.getAllByArea()
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
    }
    state<CommunicateWithUser> {
        onEnter {
            sendTextMessage(
                it,
                Strings.ToAskUser.message("profile"), // поменять на переменную профиля участника (берем из бд)
                replyMarkup = replyKeyboard(
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                ) {
                    row {
                        simpleButton(ButtonStrings.Yes)
                        simpleButton(ButtonStrings.No)
                    }
                }
            )
        }
        onText(ButtonStrings.Yes) {
            sendTextMessage(it.chat, Strings.WriteToCompanion)
            sendTextMessage(it.chat, Strings.CopyQuestion)
            sendTextMessage(it.chat, "question") // тут отправка вопроса (берем из бд)
            // отправлять инфу о согласии тому, кто вызывался ответить на вопрос
            // думаю, что нужно переходить в др состояние
        }
        onText(ButtonStrings.No) {
            // отправлять инфу о несогласии тому, кто вызывался ответить на вопрос
            // думаю, что нужно переходить в др состояние
        }
    }
}
