package mute.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import mute.Strings
import mute.domain.usecases.DeleteMuteSettingsUseCase
import mute.domain.usecases.GetEarliestMuteSettingsUseCase
import mute.telegram.queries.YesNoMuteQuery
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.hours

@Single
class UnmuteRunner(
    private val getEarliestMuteSettingsUseCase: GetEarliestMuteSettingsUseCase,
    private val deleteMuteSettingsUseCase: DeleteMuteSettingsUseCase,
    private val clock: Clock
) {
    fun BehaviourContext.unmute() = launch {
        while (true) {
            val pair = getEarliestMuteSettingsUseCase()
            if (pair != null && clock.now().compareTo(pair.until) != -1) {
                deleteMuteSettingsUseCase(pair.userId)
                sendTextMessage(
                    UserId(pair.userId),
                    Strings.unmuteQuestion,
                    replyMarkup = inlineKeyboard {
                        row {
                            dataButton(Strings.yes, YesNoMuteQuery(true))
                            dataButton(Strings.no, YesNoMuteQuery(false))
                        }
                    }
                )
            } else {
                delay(1.hours)
            }
        }
    }
}