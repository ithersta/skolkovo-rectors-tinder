package auth.telegram.flows

import auth.domain.entities.User
import auth.domain.usecases.ApproveUserUseCase
import auth.domain.usecases.DeleteUserUseCase
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
import dev.inmo.tgbotapi.extensions.utils.withContentOrThrow
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.toChatId
import generated.dataButton
import generated.onDataCallbackQuery
import notifications.telegram.admin.UserApprovalQueries
import notifications.telegram.sendNotificationPreferencesMessage
import org.koin.core.component.inject
import qna.domain.usecases.GetUserDetailsUseCase

fun RoleFilterBuilder<DialogState, User, User.Admin, UserId>.approveUserFlow() {
    val approveUserUseCase: ApproveUserUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val deleteUser: DeleteUserUseCase by inject()
    anyState {
        onDataCallbackQuery(UserApprovalQueries.Approve::class) { (data, query) ->
            val chatId = data.chatId
            approveUserUseCase.invoke(chatId)
            sendNotificationPreferencesMessage(chatId.toChatId())
            sendTextMessage(
                chatId.toChatId(),
                AccountWasVerified,
                replyMarkup = flatInlineKeyboard {
                    dataButton(StartButton, StartQuery)
                }
            )
            edit(
                query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>(),
                approvePersonInfo(getUserDetailsUseCase(chatId)!!, true),
                replyMarkup = null
            )
            answer(query)
        }
        onDataCallbackQuery(UserApprovalQueries.Disapprove::class) { (data, query) ->
            val chatId = data.chatId
            val details = getUserDetailsUseCase(chatId)
            deleteUser(chatId)
            sendTextMessage(chatId.toChatId(), AdminDoNotAccept)
            details?.let {
                edit(
                    query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>(),
                    approvePersonInfo(it, false),
                    replyMarkup = null
                )
            }
            answer(query)
        }
    }
}
