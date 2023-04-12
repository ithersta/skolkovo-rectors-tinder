package addorganizations.telegram

import addorganizations.telegram.queries.AddCityQuery
import addorganizations.telegram.queries.AddOrganizationQuery
import common.domain.Transaction
import config.BotConfig
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton


class SendConfirmAdding(
    //todo: use case for search city
    //todo: use case for search country
    private val transaction: Transaction,
    private val botConfig: BotConfig
) {
    suspend operator fun BehaviourContext.invoke(userId: Long, city: String, university: String?) {
        val havingCity = false
        transaction {
            //todo: check city
        }
        if (university == null) {
            sendTextMessage(
                UserId(botConfig.adminId!!),
                AddingStrings.confirmationAddingCityAdmin(city),
                replyMarkup = inlineKeyboard {
                    row {
                        dataButton(AddingStrings.Confirmation, AddCityQuery(userId))
                    }
                }
            )
        } else {
            sendTextMessage(
                UserId(botConfig.adminId!!),
                AddingStrings.confirmationAddingUniversityAdmin(havingCity, city, university),
                replyMarkup = inlineKeyboard {
                    row {
                        dataButton(AddingStrings.Confirmation, AddOrganizationQuery(userId))
                    }
                }
            )
        }
    }
}
