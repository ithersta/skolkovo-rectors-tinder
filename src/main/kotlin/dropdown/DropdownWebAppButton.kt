package dropdown

import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardRowBuilder
import dev.inmo.tgbotapi.extensions.utils.types.buttons.webAppButton
import io.ktor.http.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable

private const val HOST = "tg-web-apps.vercel.app"
private val client = JsonStorageClient()

@Serializable
class DropdownOption(
    val id: Long,
    val text: String
)

suspend fun ReplyKeyboardRowBuilder.dropdownWebAppButton(
    text: String,
    options: List<DropdownOption>,
    noneOption: String?,
    noneConfirmationMessage: String?
) {
    val optionsUrl = client.storeAndGetUrl(options)
    webAppButton(
        text,
        url {
            protocol = URLProtocol.HTTPS
            host = HOST
            pathSegments = listOf("dropdown")
            parameters.append("options", optionsUrl)
            noneOption?.let { parameters.append("noneOption", it) }
            noneConfirmationMessage?.let { parameters.append("noneConfirm", it) }
        }
    )
}
