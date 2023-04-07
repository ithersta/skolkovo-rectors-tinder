package menus.flows

import auth.domain.entities.User
import changeinfo.telegram.sendFieldsToChange
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.strings.accountInfo
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import menus.states.MenuState
import menus.strings.MenuStrings
import notifications.telegram.sendNotificationPreferencesMessage
import org.koin.core.component.inject
import qna.domain.usecases.GetUserDetailsUseCase

fun StateMachineBuilder<DialogState, User, UserId>.accountInfoFlow() {
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()

    role<User.Normal> {
        state<MenuState.AccountInfoState> {
            onEnter {
                val userDetails = getUserDetailsUseCase(it.chatId)
                val userInfo=  accountInfo(userDetails!!)
                sendTextMessage(
                    it,
                    userInfo,
                    replyMarkup = replyKeyboard(resizeKeyboard = true, oneTimeKeyboard = true) {
                        row {
                            simpleButton(MenuStrings.AccountInfo.Notifications)
                        }
                        row {
                            simpleButton(MenuStrings.AccountInfo.ChangeAccountInfo)
                        }
                    }
                )
            }
            onText(MenuStrings.AccountInfo.Notifications) {
                sendNotificationPreferencesMessage(it.chat.id)
            }
            onText(MenuStrings.AccountInfo.ChangeAccountInfo) {
                sendFieldsToChange(it)
            }
        }
    }
}
