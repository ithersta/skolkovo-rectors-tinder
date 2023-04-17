import addorganizations.telegram.flows.addCityOrganizationAdminFlow
import addorganizations.telegram.flows.addCityOrganizationUserFlow
import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.flows.approveUserFlow
import auth.telegram.flows.fillingAccountInfoFlow
import auth.telegram.flows.startButtonFlow
import auth.telegram.states.WaitingForContact
import changeinfo.telegram.flows.changeAccountInfoFlow
import com.ithersta.tgbotapi.boot.annotations.StateMachine
import com.ithersta.tgbotapi.commands.cancelCommand
import com.ithersta.tgbotapi.commands.fallback
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import common.telegram.Query
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import event.telegram.eventFlow
import feedback.telegram.flows.feedbackFlow
import menus.adminMenu
import menus.flows.accountInfoFlow
import menus.normalMenu
import menus.menu
import mute.telegram.flows.muteFlow
import notifications.telegram.flows.changeNotificationPreferenceFlow
import notifications.telegram.flows.testNotificationsFlow
import qna.telegram.flows.*

@StateMachine(baseQueryKClass = Query::class)
val stateMachine = stateMachine<DialogState, User, UserId>(
    initialState = DialogState.Empty,
    includeHelp = true
) {
    cancelCommand(initialState = DialogState.Empty)

    role<User.Unauthenticated> {
        fillingAccountInfoFlow()
        addCityOrganizationUserFlow()
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
        addCityOrganizationAdminFlow()
        approveUserFlow()
    }
    role<User.Normal> {
        anyState {
            onCommand("start", null) {
                state.override { DialogState.Empty }
            }
        }
        with(normalMenu) { invoke() }
        startButtonFlow()
        feedbackFlow()
        askQuestionFlow()
        getListOfRespondentNoAnswerFlow()
        changeAccountInfoFlow()
        changeNotificationPreferenceFlow()
        questionDigestFlow()
        newResponseFlow()
        oldQuestionFlow()
        addCityOrganizationUserFlow()
    }
    accountInfoFlow()
    muteFlow()
    eventFlow()
    fallback()
}
