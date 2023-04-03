package dropdown

import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardRowBuilder
import dev.inmo.tgbotapi.extensions.utils.types.buttons.webAppButton
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
