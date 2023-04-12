import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.flows.fillingAccountInfoFlow
import auth.telegram.states.WaitingForContact
import changeinfo.telegram.flows.changeAccountInfoFlow
import com.ithersta.tgbotapi.boot.annotations.StateMachine
import com.ithersta.tgbotapi.commands.cancelCommand
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.domain.Transaction
import common.telegram.DialogState
import common.telegram.Query
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.types.UserId
import dropdown.DropdownOption
import dropdown.dropdownWebAppButton
import event.telegram.eventFlow
import feedback.telegram.flows.feedbackFlow
import menus.adminMenu
import menus.flows.accountInfoFlow
import menus.normalMenu
import mute.telegram.flows.muteFlow
import notifications.telegram.flows.changeNotificationPreferenceFlow
import notifications.telegram.flows.testNotificationsFlow
import org.koin.core.component.inject
import organizations.domain.repository.CityRepository
import qna.telegram.flows.*

@StateMachine(baseQueryKClass = Query::class)
val stateMachine = stateMachine<DialogState, User, UserId>(
    initialState = DialogState.Empty,
    includeHelp = true
) {
    cancelCommand(initialState = DialogState.Empty)

    anyRole {
        val cityRepository: CityRepository by inject()
        val transaction: Transaction by inject()
        anyState {
            onCommand("city", null) { message ->
                sendTextMessage(
                    message.chat,
                    Strings.AccountInfo.ChooseCity,
                    replyMarkup = flatReplyKeyboard {
                        dropdownWebAppButton(
                            "TODO()",
                            options = transaction { cityRepository.getAll() }.map { DropdownOption(it.id, it.name) },
                            noneConfirmationMessage = "TODO()",
                            noneOption = "TODO()"
                        )
                    }
                )
            }
        }
    }
    role<User.Unauthenticated> {
        fillingAccountInfoFlow()
        anyState {
            onCommand("start", null) {
                state.override { WaitingForContact }
                runCatching { refreshCommands() }
            }
        }
        state<DialogState.Empty> {
            onEnter {
                sendTextMessage(it, Strings.RoleMenu.Unauthenticated)
            }
        }
    }
    role<User.Admin> {
        with(adminMenu) { invoke() }
        testNotificationsFlow()
    }
    role<User.Normal> {
        anyState {
            onCommand("start", null) {
                state.override { DialogState.Empty }
            }
        }
        with(normalMenu) { invoke() }
        feedbackFlow()
        askQuestionFlow()
        getListOfRespondentNoAnswerFlow()
        changeAccountInfoFlow()
        changeNotificationPreferenceFlow()
        questionDigestFlow()
        newResponseFlow()
        oldQuestionFlow()
    }
    accountInfoFlow()
    muteFlow()
    eventFlow()
    fallback()
}
