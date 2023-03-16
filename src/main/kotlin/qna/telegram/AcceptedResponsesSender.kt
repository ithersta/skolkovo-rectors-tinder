package qna.telegram

import common.telegram.MassSendLimiter
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import org.koin.core.annotation.Single
import qna.domain.usecases.AddAcceptedResponseUseCase
import qna.telegram.strings.Strings

@Single
class AcceptedResponsesSender(
    private val addAcceptedResponseUseCase: AddAcceptedResponseUseCase,
    private val massSendLimiter: MassSendLimiter
) {
    context(BehaviourContext)
    fun setup() = launchSafelyWithoutExceptions {
        addAcceptedResponseUseCase.newAcceptedResponses.collect { notification ->
            massSendLimiter.wait()
            runCatching {
                send(
                    notification.respondentId.toChatId(),
                    Strings.ToAnswerUser.waitingForCompanion(notification.question.subject)
                )
            }
        }
    }
}
