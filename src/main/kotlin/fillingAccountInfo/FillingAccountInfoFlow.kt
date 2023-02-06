package fillingAccountInfo

import auth.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import dev.inmo.tgbotapi.types.UserId
import states.ChooseCityState
import states.DialogState


fun RoleFilterBuilder<DialogState, User,User.Normal, UserId>.fillingAccountInfoFlow() {
    state<ChooseCityState>{
        onEnter {

        }
    }
}