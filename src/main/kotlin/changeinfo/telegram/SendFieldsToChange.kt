package changeinfo.telegram

import auth.domain.entities.User
import changeinfo.telegram.Strings.namesToQueries
import com.ithersta.tgbotapi.fsm.StatefulContext
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import generated.dataButton

suspend fun <S : User> StatefulContext<DialogState, User, *, S>
    .sendFieldsToChange(message: CommonMessage<TextContent>) {
    sendTextMessage(
        message.chat,
        Strings.ChooseFieldToChange,
        replyMarkup = inlineKeyboard {
            val names = namesToQueries.keys
            names.chunked(2) {
                row {
                    it.forEach {
                        dataButton(it, namesToQueries.getValue(it))
                    }
                }
            }
        }
    )
}
