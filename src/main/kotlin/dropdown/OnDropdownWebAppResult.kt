package dropdown

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.entities.triggers.Handler
import com.ithersta.tgbotapi.fsm.entities.triggers.onWebAppData
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.message.ChatEvents.WebAppData
import dev.inmo.tgbotapi.types.message.PrivateEventMessage
import generated.StateFilterBuilder

fun <S : DialogState, U : User> StateFilterBuilder<S, U>.onDropdownWebAppResult(
    handler: Handler<DialogState, User, S, U, Pair<PrivateEventMessage<WebAppData>, Long?>>
) = onWebAppData { message ->
    val result = message.chatEvent.data
        .takeIf { it != "null" }
        ?.toLongOrNull()
    handler(this, message to result)
}
