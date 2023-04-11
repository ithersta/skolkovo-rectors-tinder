package common.telegram.functions

import auth.domain.entities.User
import auth.telegram.Strings
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.domain.Transaction
import common.telegram.DialogState
import common.telegram.strings.DropdownWebAppStrings
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.types.UserId
import dropdown.DropdownOption
import dropdown.dropdownWebAppButton
import dropdown.onDropdownWebAppResult
import org.koin.core.component.inject
import organizations.domain.repository.CityRepository

fun <State : DialogState> StateFilterBuilder<DialogState, User, State, *, UserId>.selectCity(
    onFinish: (State, Long) -> DialogState
) {
    val cityRepository: CityRepository by inject()
    val transaction: Transaction by inject()
    onEnter {
        sendTextMessage(
            it,
            Strings.AccountInfo.ChooseCity,
            replyMarkup = flatReplyKeyboard {
                dropdownWebAppButton(
                    DropdownWebAppStrings.CityDropdown.Button,
                    options = transaction { cityRepository.getAll() }.map { DropdownOption(it.id, it.name) },
                    noneConfirmationMessage = DropdownWebAppStrings.CityDropdown.Confirmation,
                    noneOption = DropdownWebAppStrings.CityDropdown.NoCity
                )
            }
        )
    }
    onDropdownWebAppResult { (message, result) ->
        if (result != null) {
            state.override { onFinish(state.snapshot, result) }
        }
        ///TODO:потом обработчик Ивана сюда вставить
    }
}
