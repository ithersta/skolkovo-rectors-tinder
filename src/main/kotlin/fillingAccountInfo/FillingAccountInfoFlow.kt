package fillingAccountInfo

import Strings.AccountInfo.ChooseCity
import auth.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import states.ChooseCityState
import states.DialogState


fun RoleFilterBuilder<DialogState, User,User.Unauthenticated, UserId>.fillingAccountInfoFlow() {
    state<ChooseCityState>{
        onEnter {
            sendTextMessage(
                it,
                ChooseCity
            )
        }
    }
}