package auth.telegram.flows

import auth.domain.entities.User
import auth.domain.usecases.ApproveUserUseCase
import auth.telegram.Strings.AccountInfo.approvePersonInfo
import auth.telegram.Strings.AccountWasVerified
import auth.telegram.Strings.AdminDoNotAccept
import auth.telegram.Strings.StartButton
import auth.telegram.queries.StartQuery
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContent
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import generated.dataButton
import generated.onDataCallbackQuery
import notifications.telegram.admin.AdminNotice
import notifications.telegram.sendNotificationPreferencesMessage
import org.koin.core.component.inject
import qna.domain.usecases.GetUserDetailsUseCase

fun RoleFilterBuilder<DialogState, User, User.Admin, UserId>.approveUserFlow() {
    val approveUserUseCase: ApproveUserUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    anyState {
        onDataCallbackQuery(AdminNotice.AdminAnswerYes::class) { (data, query) ->
            val chatID = data.chatId
            approveUserUseCase.invoke(chatID)
            sendNotificationPreferencesMessage(chatID.toChatId())
            sendTextMessage(chatID.toChatId(), AccountWasVerified, replyMarkup = flatInlineKeyboard {
                dataButton(StartButton, StartQuery)
            })
            edit(
                query.messageCallbackQueryOrThrow().message.withContent<TextContent>()!!,
                approvePersonInfo(getUserDetailsUseCase(chatID)!!, true),
                replyMarkup = null
            )
            answer(query)
        }
        onDataCallbackQuery(AdminNotice.AdminAnswerNo::class) { (data, query) ->
            val chatID = data.chatId
            sendTextMessage(chatID.toChatId(), AdminDoNotAccept)
            edit(
                query.messageCallbackQueryOrThrow().message.withContent<TextContent>()!!,
                approvePersonInfo(getUserDetailsUseCase(chatID)!!, false),
                replyMarkup = null
            )
            answer(query)
        }
    }
}
