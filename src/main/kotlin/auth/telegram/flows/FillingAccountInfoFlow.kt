package auth.telegram.flows

import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.usecases.RegisterUserUseCase
import auth.telegram.Strings
import auth.telegram.Strings.AccountInfo.ChooseCity
import auth.telegram.Strings.AccountInfo.ChooseProfessionalAreas
import auth.telegram.Strings.AccountInfo.NoProfessionalArea
import auth.telegram.Strings.AccountInfo.NoQuestionArea
import auth.telegram.Strings.AccountInfo.WriteName
import auth.telegram.Strings.AccountInfo.WriteOrganization
import auth.telegram.Strings.AccountInfo.WriteProfession
import auth.telegram.Strings.AccountInfo.WriteProfessionalActivity
import auth.telegram.Strings.AccountInfo.WriteProfessionalArea
import auth.telegram.Strings.AccountInfo.professionalAreas
import auth.telegram.Strings.FinishChoosing
import auth.telegram.Strings.InvalidShare
import auth.telegram.Strings.Question.ChooseQuestionArea
import auth.telegram.Strings.ShareContact
import auth.telegram.Strings.Welcome
import auth.telegram.Strings.questionAreaToString
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.*
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.requestContactButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import org.koin.core.component.inject
import qna.domain.entities.QuestionArea
import queries.*
import states.*
import states.DialogState

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
            state.override {WriteNameState(phoneNumber)}
        }
        onText { sendTextMessage(it.chat, InvalidShare) }
    }

    state<WriteNameState> {
        onEnter {
            sendTextMessage(
                it,
                WriteName
            )
        }
        onText {
            val name = it.content.text
            state.override { ChooseCityState(phoneNumber, name) }
        }
    }
    state<ChooseCityState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseCity
                // //TODO: здесь внедрить часть Глеба с выбором города из сложного кнопочного меню
            )
        }
        onText {
            // //TODO: здесь внедрить часть Глеба с выбором города из сложного кнопочного меню
            val city =
                it.content.text // /мб если хранить все города листом, то город учстника хранить не словами, а номером в листе?
            state.override { WriteProfessionState(phoneNumber, name, city) }
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
            state.override { WriteOrganizationState(phoneNumber, name, city, profession) }
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
            state.override {
                ChooseProfessionalAreasState(
                    phoneNumber,
                    name,
                    city,
                    profession,
                    organization,
                    emptyList()
                )
            }
        }
    }
    state<ChooseProfessionalAreasState> {
        onEnter {
            val keyboard = inlineKeyboard {
                professionalAreas.forEach { area ->
                    row {
                        if (area in state.snapshot.professionalAreas) {
                            dataButton("✅$area", UnselectQuery(area))
                        } else {
                            dataButton(area, SelectQuery(area))
                        }
                    }
                }
                row {
                    dataButton(FinishChoosing, FinishQuery)
                }
            }
            state.snapshot.messageId?.let { id ->
                runCatching {
                    editMessageReplyMarkup(it, id, keyboard)
                }
            } ?: run {
                val message =
                    sendTextMessage(
                        it,
                        ChooseProfessionalAreas,
                        replyMarkup = keyboard
                    )
                state.overrideQuietly {
                    ChooseProfessionalAreasState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas,
                        message.messageId
                    )
                }
            }
        }
        onDataCallbackQuery(SelectQuery::class) { (data, query) ->
            if (!data.area.equals(professionalAreas.last())) {
                state.override {
                    ChooseProfessionalAreasState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas + data.area,
                        messageId
                    )
                }
            } else {
                state.override {
                    AddProfessionalAreasState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas + data.area,
                        messageId
                    )
                }
            }
            answer(query)
        }
        onDataCallbackQuery(UnselectQuery::class) { (data, query) ->
            state.override {
                ChooseProfessionalAreasState(
                    phoneNumber,
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas - data.area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(FinishQuery::class) { (_, query) ->
            if (state.snapshot.professionalAreas.isEmpty()) {
                sendTextMessage(
                    query.user.id,
                    NoProfessionalArea
                )
                state.override {
                    ChooseProfessionalAreasState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas,
                        messageId
                    )
                }
            } else {
                state.override {
                    WriteProfessionalDescriptionState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas
                    )
                }
            }
            answer(query)
        }
    }
    state<AddProfessionalAreasState> {
        onEnter {
            sendTextMessage(
                it,
                WriteProfessionalArea
            )
        }
        onText {
            val area = it.content.text
            state.override {
                ChooseProfessionalAreasState(
                    phoneNumber,
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas + area,
                    messageId
                )
            }
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
            state.override {
                ChooseQuestionAreasState(
                    phoneNumber,
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    activity,
                    emptySet()
                )
            }
        }
    }
    state<ChooseQuestionAreasState> {
        onEnter {
            val keyboard = inlineKeyboard {
                QuestionArea.values().forEach { area ->
                    row {
                        val areaToString = questionAreaToString.get(area)
                        if (area in state.snapshot.questionAreas) {
                            dataButton("✅$areaToString", UnselectQuestionQuery(area))
                        } else {
                            dataButton(areaToString!!, SelectQuestionQuery(area))
                        }
                    }
                }
                row {
                    dataButton(FinishChoosing, FinishQuestionQuery)
                }
            }
            state.snapshot.messageId?.let { id ->
                runCatching {
                    editMessageReplyMarkup(it, id, keyboard)
                }
            } ?: run {
                val message = sendTextMessage(
                    it,
                    ChooseQuestionArea,
                    replyMarkup = keyboard
                )
                state.overrideQuietly {
                    ChooseQuestionAreasState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas,
                        professionalDescription,
                        questionAreas,
                        message.messageId
                    )
                }
            }
        }
        onDataCallbackQuery(SelectQuestionQuery::class) { (data, query) ->
            state.override {
                ChooseQuestionAreasState(
                    phoneNumber,
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    professionalDescription,
                    questionAreas + data.area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(UnselectQuestionQuery::class) { (data, query) ->
            state.override {
                ChooseQuestionAreasState(
                    phoneNumber,
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    professionalDescription,
                    questionAreas - data.area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(FinishQuestionQuery::class) { (_, query) ->
            if (state.snapshot.questionAreas.isEmpty()) {
                sendTextMessage(
                    query.user.id,
                    NoQuestionArea
                )
                state.override {
                    ChooseQuestionAreasState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas,
                        professionalDescription,
                        questionAreas,
                        messageId
                    )
                }
            } else {
                state.override {
                    AddAccountInfoToDataBaseState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas,
                        professionalDescription,
                        questionAreas,
                    )
                }
            }
            answer(query)
        }
    }

    state<AddAccountInfoToDataBaseState> {
        onEnter {
            val details = User.Details(
                it.chatId,
                state.snapshot.phoneNumber,
                state.snapshot.name, state.snapshot.city,
                state.snapshot.profession,
                state.snapshot.organization,
                state.snapshot.professionalAreas.joinToString(),
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
            sendTextMessage(
                it,
                resultResponse
            )
            if (resultResponse.equals(NoProfessionalArea)) {
                state.override {
                    ChooseQuestionAreasState(
                        phoneNumber,
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas,
                        professionalDescription,
                        questionAreas
                    )
                }
            } else {
                state.override { DialogState.Empty }
            }
        }
    }
}
