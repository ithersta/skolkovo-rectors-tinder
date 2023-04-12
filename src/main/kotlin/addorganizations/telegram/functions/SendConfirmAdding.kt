package addorganizations.telegram.functions

import addorganizations.telegram.AddingStrings
import addorganizations.telegram.queries.AddCityQuery
import addorganizations.telegram.queries.AddOrganizationQuery
import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.BaseStatefulContext
import com.ithersta.tgbotapi.fsm.StatefulContext
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton

suspend fun <State : DialogState, Role : User> BaseStatefulContext<DialogState, User, State, Role>.sendConfirmAdding(
    userId: Long, adminId: Long, city: String, havingCity: Boolean, organization: String?
) {
    if (organization == null) {
        sendTextMessage(
            UserId(adminId),
            AddingStrings.confirmationAddingCityAdmin(city),
            replyMarkup = inlineKeyboard {
                row {
                    dataButton(AddingStrings.Confirmation, AddCityQuery(userId))
                }
            }
        )
    } else {
        sendTextMessage(
            UserId(adminId),
            AddingStrings.confirmationAddingOrganizationAdmin(havingCity, city, organization),
            replyMarkup = inlineKeyboard {
                row {
                    dataButton(AddingStrings.Confirmation, AddOrganizationQuery(userId))
                }
            }
        )
    }
}

