package auth.telegram.flows

import addorganizations.telegram.states.AddCityUserState
import addorganizations.telegram.states.AddOrganizationUserState
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.usecases.PhoneNumberIsAllowedUseCase
import auth.domain.usecases.RegisterUserUseCase
import auth.telegram.Strings.AccountInfo.ChooseProfessionalAreas
import auth.telegram.Strings.AccountInfo.WriteName
import auth.telegram.Strings.AccountInfo.WriteProfession
import auth.telegram.Strings.AccountInfo.WriteProfessionalActivity
import auth.telegram.Strings.AccountInfo.writePersonInfo
import auth.telegram.Strings.ApproveButton
import auth.telegram.Strings.AuthenticationResults
import auth.telegram.Strings.Courses.ChooseCourse
import auth.telegram.Strings.DisapproveButton
import auth.telegram.Strings.InvalidShare
import auth.telegram.Strings.OrganizationTypes.ChooseOrganizationType
import auth.telegram.Strings.ShareContact
import auth.telegram.Strings.Welcome
import auth.telegram.Strings.courseToString
import auth.telegram.queries.ChooseCourseQuery
import auth.telegram.states.*
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onContact
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.functions.*
import common.telegram.strings.DropdownWebAppStrings
import config.BotConfig
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.requestContactButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import notifications.telegram.admin.UserApprovalQueries
import org.koin.core.component.inject

fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.fillingAccountInfoFlow() {
    val botConfig: BotConfig by inject()
    val phoneNumberIsAllowedUseCase: PhoneNumberIsAllowedUseCase by inject()
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
            when (phoneNumberIsAllowedUseCase(message.chat.id.chatId, phoneNumber)) {
                PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber -> {
                    sendTextMessage(message.chat, AuthenticationResults.DuplicatePhoneNumber)
                    state.override { DialogState.Empty }
                }

                PhoneNumberIsAllowedUseCase.Result.OK -> state.override { next(phoneNumber) }
            }
        }
        onText { sendTextMessage(it.chat, InvalidShare) }
    }

    state<ChooseCourseState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseCourse,
                replyMarkup = inlineKeyboard {
                    courseToString.map {
                        row {
                            dataButton(it.value, ChooseCourseQuery(it.key))
                        }
                    }
                }
            )
        }
        onDataCallbackQuery(ChooseCourseQuery::class) { (data, query) ->
            state.override { next(data.course) }
            answer(query)
        }
    }

    state<WriteNameState> {
        onEnter { sendTextMessage(it, WriteName) }
        onText { User.Name.fromMessage(it) { state.override { next(it) } } }
    }

    state<ChooseCity> {
        selectCity(
            stringsCity = DropdownWebAppStrings.CityDropdown,
            onFinish = { _, state, city -> state.next(city) },
            onNone = { AddCityUserState() }
        )
    }

    state<WriteProfessionState> {
        onEnter { sendTextMessage(it, WriteProfession, replyMarkup = ReplyKeyboardRemove()) }
        onText { state.override { next(it.content.text) } }
    }

    state<ChooseOrganizationTypeState> {
        chooseOrganizationType(
            text = ChooseOrganizationType,
            onFinish = { state, type -> state.next(type) }
        )
    }

    state<WriteOrganizationState> {
        selectOrganization(
            stringsOrganization = DropdownWebAppStrings.OrganizationDropdown,
            cityId = { it.cityId },
            onFinish = { state, organization -> state.next(organization) },
            onNone = { AddOrganizationUserState(it.cityId) }
        )
    }

    state<WriteProfessionalDescriptionState> {
        onEnter { sendTextMessage(it, WriteProfessionalActivity, replyMarkup = ReplyKeyboardRemove()) }
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
            val details = User.NewDetails(
                it.chatId,
                state.snapshot.phoneNumber,
                state.snapshot.course,
                state.snapshot.name,
                state.snapshot.profession,
                state.snapshot.cityId,
                state.snapshot.organizationType,
                state.snapshot.organization,
                state.snapshot.professionalDescription,
                state.snapshot.questionAreas
            )

            val resultResponse = when (val result = registerUserUseCase(details)) {
                RegisterUserUseCase.Result.DuplicatePhoneNumber ->
                    AuthenticationResults.DuplicatePhoneNumber

                RegisterUserUseCase.Result.AlreadyRegistered ->
                    AuthenticationResults.AlreadyRegistered

                RegisterUserUseCase.Result.NoAreasSet ->
                    AuthenticationResults.NoAreaSet

                RegisterUserUseCase.Result.OK ->
                    AuthenticationResults.OK

                is RegisterUserUseCase.Result.RequiresApproval -> {
                    sendTextMessage(
                        botConfig.adminId.toChatId(),
                        writePersonInfo(result.userDetails),
                        replyMarkup = confirmationInlineKeyboard(
                            positiveData = UserApprovalQueries.Approve(details.id),
                            negativeData = UserApprovalQueries.Disapprove(details.id),
                            positiveText = ApproveButton,
                            negativeText = DisapproveButton
                        )
                    )
                    AuthenticationResults.RequiresApproval
                }
            }
            sendTextMessage(it, resultResponse)
        }
    }
}
