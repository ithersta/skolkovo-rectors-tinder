package flows

import Strings
import auth.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import states.DialogState
import states.MeetingState

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.meetingFlow() {
    state<MeetingState> {
        onEnter {
            sendTextMessage(
                it,
                Strings.MeetingInfo.linkInEvent
            )
        }
    }
}
