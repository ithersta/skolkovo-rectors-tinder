package mute.telegram

import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
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
    fun BehaviourContext.setup() = launchSafelyWithoutExceptions {
        while (true) {
            val earliestRow = getEarliestMuteSettingsUseCase()
            if (earliestRow != null && clock.now().compareTo(earliestRow.until) != -1) {
                deleteMuteSettingsUseCase(earliestRow.userId)
                runCatching {
                    sendTextMessage(
                        UserId(earliestRow.userId),
                        Strings.UnmuteQuestion,
                        replyMarkup = inlineKeyboard {
                            row {
                                dataButton(Strings.Yes, YesNoMuteQuery(true))
                                dataButton(Strings.No, YesNoMuteQuery(false))
                            }
                        }
                    )
                }
            } else {
                delay(1.hours)
            }
        }
    }
}
