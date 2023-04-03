package menus.functions

import common.telegram.strings.accountInfo
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.textsources.TextSourcesList
import org.koin.core.context.GlobalContext
import qna.domain.usecases.GetUserDetailsUseCase

val getUserDetailsUseCase: GetUserDetailsUseCase by GlobalContext.get().inject()

fun getAccountInfoById(userId:UserId): TextSourcesList {
    val userDetails=getUserDetailsUseCase(userId.chatId)
    return accountInfo(userDetails!!)
}
