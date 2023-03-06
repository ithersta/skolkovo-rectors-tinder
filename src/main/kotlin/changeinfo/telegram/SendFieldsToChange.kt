package changeinfo.telegram

import auth.domain.entities.User
import changeinfo.Strings.namesToQueries
import com.ithersta.tgbotapi.fsm.StatefulContext
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import org.koin.core.context.GlobalContext
import qna.domain.usecases.GetUserDetailsUseCase
import qna.telegram.strings.Strings.accountInfo

suspend fun <S : User> StatefulContext<DialogState, User, *, S>.sendFieldsToChange(message: CommonMessage<TextContent>) {
    val getUserDetailsByIdUseCase: GetUserDetailsUseCase by GlobalContext.get().inject()
    val user = getUserDetailsByIdUseCase(message.chat.id.chatId)!!
    sendTextMessage(
        message.chat,
        accountInfo(user.name, user.city, user.job, user.organization, user.activityDescription),
        replyMarkup = ReplyKeyboardRemove()
    )
    sendTextMessage(
        message.chat,
        changeinfo.Strings.ChooseFieldToChange,
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
