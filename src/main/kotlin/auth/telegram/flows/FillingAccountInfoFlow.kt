package auth.telegram.flows

import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.usecases.RegisterUserUseCase
import auth.telegram.Strings
import auth.telegram.Strings.AccountInfo.ChooseProfessionalAreas
import auth.telegram.Strings.AccountInfo.WriteName
import auth.telegram.Strings.AccountInfo.WriteOrganization
import auth.telegram.Strings.AccountInfo.WriteProfession
import auth.telegram.Strings.AccountInfo.WriteProfessionalActivity
import auth.telegram.Strings.InvalidShare
import auth.telegram.Strings.ShareContact
import auth.telegram.Strings.Welcome
import auth.telegram.parsers.JsonParser
import auth.telegram.queries.*
import auth.telegram.states.*
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onContact
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.requestContactButton
import dev.inmo.tgbotapi.types.UserId
import generated.onDataCallbackQuery
import notifications.telegram.sendNotificationPreferencesMessage
import org.koin.core.component.inject
import qna.flows.chooseQuestionAreas

val jsonParser: JsonParser = JsonParser()

fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.fillingAccountInfoFlow() {
    val registerUserUseCase: RegisterUserUseCase by inject()

    state<WaitingForContact> {
        onEnter {
            sendTextMessage(
                it,
                Welcome,
                replyMarkup = flatReplyKeyboard(resizeKeyboard = true, oneTimeKeyboard = true) {
                    requestContactButton(ShareContact)
                }
            )
        }
        onContact { message ->
            val contact = message.content.contact
            require(contact.userId == message.chat.id)
            val phoneNumber = PhoneNumber.of(contact.phoneNumber.filter { it.isDigit() })!!
            state.override { next(phoneNumber) }
        }
        onText { sendTextMessage(it.chat, InvalidShare) }
    }

    state<WriteNameState> {
        onEnter { sendTextMessage(it, WriteName) }
        onText { state.override { next(it.content.text) } }
    }
    state<ChooseCountry> {
        onEnter {
            sendTextMessage(
                it,
                Strings.AccountInfo.ChooseCountry,
                replyMarkup = jsonParser.getCountries()
            )
        }
        onDataCallbackQuery(SelectCountryQuery::class) { (data, query) ->
            when (data.country) {
                "\uD83C\uDDF7\uD83C\uDDFA" -> state.override { chooseDistrict() }
                else -> state.override { chooseCityInCis(data.country) }
            }
            answer(query)
        }
    }
    state<ChooseCityInCis> {
        onEnter {
            sendTextMessage(
                it,
                Strings.AccountInfo.ChooseCity,
                replyMarkup = jsonParser.getCitiesFromCIS(state.snapshot.country)
            )
        }
        onDataCallbackQuery(SelectCityInCIS::class) { (data, query) ->
            state.override { next(data.city) }
            answer(query)
        }
    }
    state<ChooseDistrict> {
        onEnter {
            sendTextMessage(
                it,
                Strings.AccountInfo.ChooseDistrict,
                replyMarkup = jsonParser.getDistricts()
            )
        }
        onDataCallbackQuery(SelectDistrict::class) { (data, query) ->
            state.override { ChooseRegion(state.snapshot.phoneNumber, state.snapshot.name, data.district) }
            answer(query)
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
        onDataCallbackQuery(SelectRegion::class) { (data, query) ->
            state.override { ChooseCity(state.snapshot.phoneNumber, state.snapshot.name, data.region) }
            answer(query)
        }
    }
    state<ChooseCity> {
        onEnter {
            sendTextMessage(
                it,
                Strings.AccountInfo.ChooseCity,
                replyMarkup = jsonParser.getCitiesByRegion(state.snapshot.region)
            )
        }
        onDataCallbackQuery(SelectCity::class) { (data, query) ->
            val city: String = data.city
            if (jsonParser.cityRegex.matches(city)) {
                state.override { WriteProfessionState(state.snapshot.phoneNumber, state.snapshot.name, city) }
            }
            answer(query)
        }
    }

    state<WriteProfessionState> {
        onEnter { sendTextMessage(it, WriteProfession) }
        onText { state.override { next(it.content.text) } }
    }
    state<WriteOrganizationState> {
        onEnter { sendTextMessage(it, WriteOrganization) }
        onText { state.override { next(it.content.text) } }
    }
    state<WriteProfessionalDescriptionState> {
        onEnter { sendTextMessage(it, WriteProfessionalActivity) }
        onText { state.override { next(it.content.text) } }
    }
    state<ChooseQuestionAreasState> {
        chooseQuestionAreas(
            text = ChooseProfessionalAreas,
            getAreas = { it.questionAreas },
            getMessageId = { it.messageId },
            onSelectionChanged = { state, areas -> state.copy(questionAreas = areas) },
            onMessageIdSet = { state, messageId -> state.copy(messageId = messageId) },
            onFinish = { it.next() }
        )
    }

    state<AddAccountInfoToDataBaseState> {
        onEnter {
            val details = User.Details(
                it.chatId,
                state.snapshot.phoneNumber,
                state.snapshot.name,
                state.snapshot.city,
                state.snapshot.profession,
                state.snapshot.organization,
                state.snapshot.professionalDescription,
                state.snapshot.questionAreas
            )

            val resultResponse = when (registerUserUseCase(details)) {
                RegisterUserUseCase.Result.DuplicatePhoneNumber ->
                    Strings.AuthenticationResults.DuplicatePhoneNumber

                RegisterUserUseCase.Result.AlreadyRegistered ->
                    Strings.AuthenticationResults.AlreadyRegistered

                RegisterUserUseCase.Result.PhoneNumberNotAllowed ->
                    Strings.AuthenticationResults.PhoneNumberNotAllowed

                RegisterUserUseCase.Result.OK ->
                    Strings.AuthenticationResults.OK

                RegisterUserUseCase.Result.NoAreasSet ->
                    Strings.AuthenticationResults.NoAreaSet
            }
            sendTextMessage(it, resultResponse)
            sendNotificationPreferencesMessage(it)
            state.override { DialogState.Empty }
        }
    }
}
