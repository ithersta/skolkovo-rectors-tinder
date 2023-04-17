package qna.telegram

import common.telegram.MassSendLimiter
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import dev.inmo.tgbotapi.types.toChatId
import generated.dataButton
import io.github.oshai.KotlinLogging
import org.koin.core.annotation.Single
import qna.domain.usecases.GetNewResponseNotificationFlowUseCase
import qna.telegram.queries.NewResponsesQuery
import qna.telegram.strings.Strings

private val logger = KotlinLogging.logger {}

@Single
class NewResponsesSender(
    private val getNewResponseNotificationFlow: GetNewResponseNotificationFlowUseCase,
    private val massSendLimiter: MassSendLimiter
) {
    fun BehaviourContext.setup() = launchSafelyWithoutExceptions {
        getNewResponseNotificationFlow().collect { notification ->
            massSendLimiter.wait()
            runCatching {
                sendTextMessage(
                    chatId = notification.question.authorId.toChatId(),
                    entities = Strings.NewResponses.message(notification),
                    replyMarkup = flatInlineKeyboard {
                        dataButton(Strings.NewResponses.SeeButton, NewResponsesQuery.SeeNew(notification.question.id!!))
                    }
                )
            }.onFailure { exception ->
                logger.error("Couldn't send new responses to ${notification.question.authorId}", exception)
            }
        }
    }
}
