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
import common.telegram.DialogState
import common.telegram.Query
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import event.telegram.eventFlow
import menus.adminMenu
import menus.normalMenu
import mute.telegram.flows.muteFlow
import notifications.telegram.flows.changeNotificationPreferenceFlow
import notifications.telegram.flows.newQuestionsNotificationFlow
import qna.telegram.flows.askQuestionFlow
import qna.telegram.flows.feedbackFlow

@StateMachine(baseQueryKClass = Query::class)
val stateMachine = stateMachine<DialogState, User, UserId>(
    initialState = DialogState.Empty,
    includeHelp = true
) {
    cancelCommand(initialState = DialogState.Empty)

    role<User.Unauthenticated> {
        fillingAccountInfoFlow()
        anyState { onCommand("start", null) { state.override { WaitingForContact } } }
        state<DialogState.Empty> { onEnter { sendTextMessage(it, Strings.RoleMenu.Unauthenticated) } }
    }
    role<User.Normal> {
        with(normalMenu) { invoke() }
        feedbackFlow()
        askQuestionFlow()
        changeAccountInfoFlow()
        changeNotificationPreferenceFlow()
        newQuestionsNotificationFlow()
    }
    role<User.Admin> { with(adminMenu) { invoke() } }
    muteFlow()
    eventFlow()
    fallback()
}
