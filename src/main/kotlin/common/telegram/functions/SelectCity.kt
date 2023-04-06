package common.telegram.functions

import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.queries.*
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.domain.Transaction
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.types.UserId
import dropdown.dropdownWebAppButton
import generated.onDataCallbackQuery
import org.koin.core.component.inject
import organizations.domain.repository.CityRepository

fun <State : DialogState> StateFilterBuilder<DialogState, User, State, *, UserId>.selectCity(
    onFinish: (State, String) -> DialogState
) {
    val cityRepository: CityRepository by inject()
    val transaction: Transaction by inject()
    onEnter {
        sendTextMessage(
            it,
            Strings.AccountInfo.ChooseCity,
            replyMarkup = flatReplyKeyboard {
                dropdownWebAppButton("dsf", options = transaction { cityRepository.getAll() }.map { it.name }, noneConfirmationMessage = "ТОЧНО??", noneOption = "МОЕГО ГОРОДА НЕТ В СПИСКЕ")
            }
        )
    }
    onDataCallbackQuery(SelectCountryQuery::class) { (data, query) ->
        when (data.country) {
            "\uD83C\uDDF7\uD83C\uDDFA" -> {
                sendTextMessage(
                    query.from.id,
                    Strings.AccountInfo.ChooseDistrict,
                    replyMarkup = TODO()
                )
            }

            else -> {
                sendTextMessage(
                    query.from.id,
                    Strings.AccountInfo.ChooseCity,
                    replyMarkup = TODO()
                )
            }
        }
        answer(query)
    }
    onDataCallbackQuery(SelectCityInCISQuery::class) { (data, query) ->
        val city: String = data.city
        if (TODO()) {
            state.override { onFinish(state.snapshot, city) }
        }
        answer(query)
    }

    onDataCallbackQuery(SelectDistrictQuery::class) { (data, query) ->
        sendTextMessage(
            query.from.id,
            Strings.AccountInfo.ChooseRegion,
            replyMarkup = TODO()
        )
        answer(query)
    }

    onDataCallbackQuery(SelectRegionQuery::class) { (data, query) ->
        sendTextMessage(
            query.from.id,
            Strings.AccountInfo.ChooseCity,
            replyMarkup = TODO()
        )
        answer(query)
    }

    onDataCallbackQuery(SelectCityQuery::class) { (data, query) ->
        val city: String = data.city
        if (TODO()) {
            state.override { onFinish(state.snapshot, city) }
        }
        answer(query)
    }
}
