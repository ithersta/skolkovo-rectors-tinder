package flows

import Strings
import auth.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import services.parsers.WebPageParser
import states.DialogState
import states.MeetingState


val webPageParser: WebPageParser = WebPageParser()


fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.meetingFlow() {
    state<MeetingState> {
        onEnter {
            sendTextMessage(
                it, Strings.MeetingInfo.info
            )
        }
    }
}


