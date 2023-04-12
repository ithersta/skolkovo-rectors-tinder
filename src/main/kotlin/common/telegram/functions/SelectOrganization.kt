package common.telegram.functions

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.domain.Transaction
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.types.UserId
import dropdown.DropdownOption
import dropdown.dropdownWebAppButton
import dropdown.onDropdownWebAppResult
import org.koin.core.component.inject
import organizations.domain.repository.OrganizationRepository

data class StringsOrganization(
    val chooseOrganization: String,
    val button: String,
    val confirmation: String,
    val noOrganization: String
)

fun <State : DialogState> StateFilterBuilder<DialogState, User, State, *, UserId>.selectOrganization(
    stringsOrganization: StringsOrganization,
    cityId: (State) -> Long?,
    onFinish: (State, Long) -> DialogState,
    onNone: (State) -> DialogState
) {
    val organizationRepository: OrganizationRepository by inject()
    val transaction: Transaction by inject()
    onEnter {
        sendTextMessage(
            it,
            stringsOrganization.chooseOrganization,
            replyMarkup = flatReplyKeyboard(oneTimeKeyboard = true) {
                dropdownWebAppButton(
                    stringsOrganization.button,
                    options = transaction {
                        if (cityId((state.snapshot)) != null) {
                            organizationRepository.getByCityId(cityId((state.snapshot))!!)
                        } else {
                            organizationRepository.getAll()
                        }
                    }.map { DropdownOption(it.id, it.name) },
                    noneConfirmationMessage = stringsOrganization.confirmation,
                    noneOption = stringsOrganization.noOrganization
                )
            }
        )
    }
    onDropdownWebAppResult { (_, result) ->
        if (result != null) {
            state.override { onFinish(state.snapshot, result) }
        } else {
            state.override { onNone(state.snapshot) }
        }
        //TODO:потом обработчик Ивана сюда вставить
    }
}
