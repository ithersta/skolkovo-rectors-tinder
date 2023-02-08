package flows

import Strings
import Strings.AccountInfo.ChooseProfessionalAreas
import Strings.AccountInfo.WriteOrganization
import Strings.AccountInfo.WriteProfession
import Strings.AccountInfo.WriteProfessionalActivity
import Strings.Question.ChooseQuestionArea
import auth.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onDataCallbackQuery
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import services.parsers.JsonParser
import states.*

val jsonParser: JsonParser = JsonParser()

fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.fillingAccountInfoFlow() {
    state<ChooseCountry> {
        onEnter {
            sendTextMessage(
                it, Strings.AccountInfo.ChooseCountry, replyMarkup = jsonParser.getCountries()
            )
        }
        onDataCallbackQuery(Regex("🇷🇺")) { state.override { ChooseDistrict(it.data) } }
        onDataCallbackQuery(Regex("🇰🇿")) { state.override { ChooseCityInCIS(it.data) } }
        onDataCallbackQuery(Regex("🇺🇿")) { state.override { ChooseCityInCIS(it.data) } }
    }
    state<ChooseCityInCIS> {
        onEnter {
            sendTextMessage(
                it, Strings.AccountInfo.ChooseCity, replyMarkup = jsonParser.getCitiesFromCIS(state.snapshot.county)
            )
        }
        onDataCallbackQuery(Regex("\\W+")) {
            val city = it.data
            state.override { WriteProfessionState(city) }
        }
    }

    state<ChooseDistrict> {
        onEnter {
            sendTextMessage(
                it, Strings.AccountInfo.ChooseDistrict, replyMarkup = jsonParser.getDistricts()
            )
        }
        onDataCallbackQuery(Regex("\\W+")) {
            val district = it.data
            state.override { ChooseRegion(district) }
        }
    }
    state<ChooseRegion> {
        onEnter {
            sendTextMessage(
                it,
                Strings.AccountInfo.ChooseRegion,
                replyMarkup = jsonParser.getRegionsByDistrict(state.snapshot.district)
            )
        }
        onDataCallbackQuery(Regex("\\W+")) {
            val region = it.data
            state.override { ChooseCity(region) }
        }
    }
    state<ChooseCity> {
        onEnter {
            sendTextMessage(
                it, Strings.AccountInfo.ChooseCity, replyMarkup = jsonParser.getCitiesByRegion(state.snapshot.region)
            )
        }
        onDataCallbackQuery(Regex("\\W+")) {
            val city = it.data
            state.override { WriteProfessionState(city) }
        }
    }
    state<WriteProfessionState> {
        onEnter {
            sendTextMessage(
                it,
                WriteProfession
            )
        }
        onText {
            val profession = it.content.text // мб валидация нужна какая-нибудь?
            state.override { WriteOrganizationState(city, profession) }
        }
    }
    state<WriteOrganizationState> {
        onEnter {
            sendTextMessage(
                it,
                WriteOrganization
            )
        }
        onText {
            val organization = it.content.text // мб валидация нужна какая-нибудь?
            state.override { ChooseProfessionalAreasState(city, profession, organization) }
        }
    }
    state<ChooseProfessionalAreasState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseProfessionalAreas
                // //TODO: где-то тут видимо прикол с множественным выбором и видимо обработчиками
                // /!!!!обработчик "Другое" просит ввода и принимает его
            )
            // //после того, как все нужные сферы выбраны, переходим в следующее состояние
            state.override {
                WriteProfessionalDescriptionState(
                    city,
                    profession,
                    organization,
                    emptyList()
                )
            } // пока пусть эмпти будет
        }
    }
    state<WriteProfessionalDescriptionState> {
        onEnter {
            sendTextMessage(
                it,
                WriteProfessionalActivity
            )
        }
        onText {
            val activity = it.content.text // мб валидация нужна какая-нибудь?
            state.override { ChooseQuestionAreasState(city, profession, organization, professionalAreas, activity) }
        }
    }
    state<ChooseQuestionAreasState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseQuestionArea
                // /ну тут видимо опять прикол с множественным выбором инлайн кнопок и обработчиками
            )
        }
        // /создание пользователя в базе данных
        // /ну я думаю, обработчик будет собирать все номера в лист, а потом создавать в базе им штучки
    }
}
