package common.telegram.functions

import com.ithersta.tgbotapi.fsm.BaseStatefulContext
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.types.message.abstracts.Message
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun BaseStatefulContext<*, *, *, *>.deleteAfterDelay(
    message: Message,
    delay: Duration = 5.seconds
) = coroutineScope.launchSafelyWithoutExceptions {
    delay(delay)
    delete(message)
}
