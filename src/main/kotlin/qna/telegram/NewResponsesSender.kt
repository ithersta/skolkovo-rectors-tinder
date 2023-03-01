package qna.telegram

import common.telegram.MassSendLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import qna.domain.usecases.GetNewResponseNotificationFlowUseCase

@Single
class NewResponsesSender(
    private val getNewResponseNotificationFlow: GetNewResponseNotificationFlowUseCase,
    private val massSendLimiter: MassSendLimiter
) {
    fun BehaviourContext.setup() = launch {
        getNewResponseNotificationFlow().collect { notification ->
            massSendLimiter.wait()
            runCatching {
                TODO()
            }
        }
    }
}
