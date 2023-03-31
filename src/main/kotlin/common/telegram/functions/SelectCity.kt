package common.telegram.functions

import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.flows.jsonParser
import auth.telegram.queries.*
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import generated.onDataCallbackQuery

fun <State : DialogState> StateFilterBuilder<DialogState, User, State, *, UserId>.selectCity(
    onFinish: (State, String) -> DialogState
) {
    onEnter {
        sendTextMessage(
            it,
            Strings.AccountInfo.ChooseCountry,
            replyMarkup = jsonParser.getCountries()
        )
    }
    onDataCallbackQuery(SelectCountryQuery::class) { (data, query) ->
        when (data.country) {
            "\uD83C\uDDF7\uD83C\uDDFA" -> {
                sendTextMessage(
                    query.from.id,
                    Strings.AccountInfo.ChooseDistrict,
                    replyMarkup = jsonParser.getDistricts()
                )
            }

            else -> {
                sendTextMessage(
                    query.from.id,
                    Strings.AccountInfo.ChooseCity,
                    replyMarkup = jsonParser.getCitiesFromCIS(data.country)
                )
            }
        }
        answer(query)
    }
    onDataCallbackQuery(SelectCityInCISQuery::class) { (data, query) ->
        val city: String = data.city
        if (jsonParser.cityRegex.matches(city)) {
            state.override { onFinish(state.snapshot, city) }
        }
        answer(query)
    }

    onDataCallbackQuery(SelectDistrictQuery::class) { (data, query) ->
        sendTextMessage(
            query.from.id,
            Strings.AccountInfo.ChooseRegion,
            replyMarkup = jsonParser.getRegionsByDistrict(data.district)
        )
        answer(query)
    }

    onDataCallbackQuery(SelectRegionQuery::class) { (data, query) ->
        sendTextMessage(
            query.from.id,
            Strings.AccountInfo.ChooseCity,
            replyMarkup = jsonParser.getCitiesByRegion(data.region)
        )
        answer(query)
    }

    onDataCallbackQuery(SelectCityQuery::class) { (data, query) ->
        val city: String = data.city
        if (jsonParser.cityRegex.matches(city)) {
            state.override { onFinish(state.snapshot, city) }
        }
        answer(query)
    }
    onDataCallbackQuery(SelectUniversityQuery::class) { (data, query) ->
    }

}
