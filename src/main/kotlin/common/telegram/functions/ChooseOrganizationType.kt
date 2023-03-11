package common.telegram.functions

import auth.domain.entities.OrganizationType
import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.queries.ChooseOrganizationTypeQuery
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery

fun <State : DialogState> StateFilterBuilder<DialogState, User, State, *, UserId>.chooseOrganizationType(
    text: String,
    onFinish: (State, OrganizationType) -> DialogState
) {
    onEnter {
        sendTextMessage(
            it,
            text,
            replyMarkup = inlineKeyboard {
                Strings.organizationTypeToString.map {
                    row {
                        dataButton(it.value, ChooseOrganizationTypeQuery(it.key))
                    }
                }
            }
        )
    }
    onDataCallbackQuery(ChooseOrganizationTypeQuery::class) { (data, query) ->
        state.override { onFinish(state.snapshot, data.type) }
        answer(query)
    }
}
