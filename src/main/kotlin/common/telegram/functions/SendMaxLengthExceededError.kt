package common.telegram.functions

import arrow.core.Either
import common.domain.LimitedLengthStringType
import common.domain.MaxLengthExceeded
import common.telegram.strings.CommonStrings
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.content.TextMessage

context(TelegramBot)
suspend fun <T : Any> LimitedLengthStringType<T>.fromMessage(
    message: TextMessage,
    block: suspend (T) -> Unit
) = of(message.content.text).handleMaxLengthExceededOr(message.chat, block)

context(TelegramBot)
suspend fun <T : Any> Either<MaxLengthExceeded, T>.handleMaxLengthExceededOr(
    chat: Chat,
    block: suspend (T) -> Unit
) {
    when (this) {
        is Either.Right -> block(value)
        is Either.Left -> send(chat, CommonStrings.maxLengthExceeded(value.maxLength))
    }
}
