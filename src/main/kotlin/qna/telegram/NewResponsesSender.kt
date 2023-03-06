package qna.telegram

import common.telegram.MassSendLimiter
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import dev.inmo.tgbotapi.types.toChatId
import generated.dataButton
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import qna.domain.usecases.GetNewResponseNotificationFlowUseCase
import qna.telegram.queries.NewResponsesQuery
import qna.telegram.strings.Strings

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
                    text = Strings.NewResponses.message(notification),
                    replyMarkup = flatInlineKeyboard {
                        dataButton(Strings.NewResponses.SeeButton, NewResponsesQuery.SeeNew(notification.question.id!!))
                    }
                )
            }
        }
    }
}
