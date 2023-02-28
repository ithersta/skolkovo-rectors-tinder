package qna.telegram

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import qna.domain.usecases.GetNewResponseNotificationFlowUseCase

@Single
class NewResponsesSender(
    private val getNewResponseNotificationFlow: GetNewResponseNotificationFlowUseCase
) {
    fun BehaviourContext.setup() = launch {
        getNewResponseNotificationFlow().collect { notification ->
            runCatching {
                TODO()
            }
        }
    }
}
