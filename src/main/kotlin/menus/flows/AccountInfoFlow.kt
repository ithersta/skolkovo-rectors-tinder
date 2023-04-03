package menus.flows

import auth.domain.entities.User
import changeinfo.telegram.sendFieldsToChange
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import menus.functions.GetAccountInfoById
import menus.states.MenuState
import menus.strings.MenuStrings
import notifications.telegram.sendNotificationPreferencesMessage

fun StateMachineBuilder<DialogState, User, UserId>.accountInfoFlow() {
    role<User.Normal> {
        state<MenuState.AccountInfoState> {
            onEnter {
                sendTextMessage(
                    it,
                    GetAccountInfoById(it),
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
