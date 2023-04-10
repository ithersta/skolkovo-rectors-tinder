package adduniversity.telegram

import adduniversity.telegram.queries.AddCityQuery
import adduniversity.telegram.queries.AddUniversityQuery
import common.domain.Transaction
import config.BotConfig
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import mute.telegram.queries.YesNoMuteQuery


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
                Strings.confirmationAddingCityAdmin(city),
                replyMarkup = inlineKeyboard {
                    row {
                        dataButton(Strings.confirmation, AddCityQuery(userId))
                    }
                }
            )
        } else {
            sendTextMessage(
                UserId(botConfig.adminId!!),
                Strings.confirmationAddingUniversityAdmin(havingCity, city, university),
                replyMarkup = inlineKeyboard {
                    row {
                        dataButton(Strings.confirmation, AddUniversityQuery(userId))
                    }
                }
            )
        }
    }
}
