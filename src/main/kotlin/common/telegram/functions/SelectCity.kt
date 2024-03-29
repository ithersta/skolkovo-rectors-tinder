package common.telegram.functions

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.domain.Transaction
import common.telegram.DialogState
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dropdown.DropdownOption
import dropdown.dropdownWebAppButton
import dropdown.onDropdownWebAppResult
import org.koin.core.component.inject
import organizations.domain.repository.CityRepository

fun <State : DialogState> StateFilterBuilder<DialogState, User, State, *, UserId>.selectCity(
    stringsCity: StringsCity,
    onFinish: suspend TelegramBot.(ChatId, State, Long) -> DialogState,
    onNone: (State) -> DialogState
) {
    val cityRepository: CityRepository by inject()
    val transaction: Transaction by inject()
    onEnter {
        sendTextMessage(
            it,
            stringsCity.chooseCity,
            replyMarkup = flatReplyKeyboard(oneTimeKeyboard = true) {
                dropdownWebAppButton(
                    stringsCity.button,
                    options = transaction { cityRepository.getAll() }.map { DropdownOption(it.id, it.name.value) },
                    noneConfirmationMessage = stringsCity.confirmation,
                    noneOption = stringsCity.noCity
                )
            }
        )
    }
    onDropdownWebAppResult { (message, result) ->
        if (result != null) {
            val newState = onFinish(message.chat.id, state.snapshot, result)
            state.override { newState }
        } else {
            state.override { onNone(state.snapshot) }
        }
    }
}

data class StringsCity(
    val chooseCity: String,
    val button: String,
    val confirmation: String?,
    val noCity: String?
)
