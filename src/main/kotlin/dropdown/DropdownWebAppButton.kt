package dropdown

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.entities.triggers.Handler
import com.ithersta.tgbotapi.fsm.entities.triggers.onWebAppData
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardRowBuilder
import dev.inmo.tgbotapi.extensions.utils.types.buttons.webAppButton
import dev.inmo.tgbotapi.types.message.ChatEvents.WebAppData
import dev.inmo.tgbotapi.types.message.PrivateEventMessage
import generated.StateFilterBuilder
import io.ktor.http.*
import io.ktor.server.util.*

private const val HOST = "tg-web-apps.vercel.app"

fun ReplyKeyboardRowBuilder.dropdownWebAppButton(
    text: String,
    options: List<String>,
    noneOption: String?,
    noneConfirmationMessage: String?
) {
    webAppButton(text, url {
        protocol = URLProtocol.HTTPS
        host = HOST
        pathSegments = listOf("dropdown")
        parameters.append("options", options.joinToString(separator = "|"))
        noneOption?.let { parameters.append("noneOption", it) }
        noneConfirmationMessage?.let { parameters.append("noneConfirm", it) }
    })
}

fun <S : DialogState, U : User> StateFilterBuilder<S, U>.onDropdownWebAppResult(
    handler: Handler<DialogState, User, S, U, Pair<PrivateEventMessage<WebAppData>, String?>>
) = onWebAppData { message ->
    val result = message.chatEvent.data
        .takeIf { it != "null" }
        ?.removeSurrounding("\"")
    handler(this, message to result)
}
