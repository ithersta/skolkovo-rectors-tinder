package auth.telegram.flows

import auth.data.tables.Users
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.usecases.PhoneNumberIsAllowedUseCase
import auth.domain.usecases.RegisterUserUseCase
import auth.telegram.Strings.AccountInfo.AdminDoNotAccept
import auth.telegram.Strings.AccountInfo.ChooseProfessionalAreas
import auth.telegram.Strings.AccountInfo.PersonWantsAdd
import auth.telegram.Strings.AccountInfo.WriteName
import auth.telegram.Strings.AccountInfo.WriteOrganization
import auth.telegram.Strings.AccountInfo.WriteProfession
import auth.telegram.Strings.AccountInfo.WriteProfessionalActivity
import auth.telegram.Strings.AuthenticationResults
import auth.telegram.Strings.Courses.ChooseCourse
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
import common.telegram.functions.chooseOrganizationType
import common.telegram.functions.chooseQuestionAreas
import common.telegram.functions.confirmationInlineKeyboard
import common.telegram.functions.selectCity
import config.BotConfig
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatReplyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.requestContactButton
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import notifications.telegram.admin.AdminNotice
import notifications.telegram.sendNotificationPreferencesMessage
import org.jetbrains.exposed.sql.update
import org.koin.core.component.inject

fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.fillingAccountInfoFlow() {
    val botConfig: BotConfig
    val registerUserUseCase: RegisterUserUseCase by inject()
    val phoneNumberIsAllowedUseCase: PhoneNumberIsAllowedUseCase by inject()

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
        onText { state.override { next(it.content.text) } }
    }

    state<ChooseCity> {
        selectCity(
            onFinish = { state, city -> state.next(city) }
        )
    }

    state<WriteProfessionState> {
        onEnter { sendTextMessage(it, WriteProfession) }
        onText { state.override { next(it.content.text) } }
    }

    state<ChooseOrganizationTypeState> {
        chooseOrganizationType(
            text = ChooseOrganizationType,
            onFinish = { state, type -> state.next(type) }
        )
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
            val details = User.NewDetails(
                it.chatId,
                state.snapshot.phoneNumber,
                state.snapshot.course,
                state.snapshot.name,
                state.snapshot.profession,
                TODO(),
                state.snapshot.organizationType,
                TODO(),
                state.snapshot.professionalDescription,
                state.snapshot.questionAreas
            )

            val resultResponse = when (registerUserUseCase(details)) {
                RegisterUserUseCase.Result.DuplicatePhoneNumber ->
                    AuthenticationResults.DuplicatePhoneNumber

                RegisterUserUseCase.Result.AlreadyRegistered ->
                    AuthenticationResults.AlreadyRegistered

                RegisterUserUseCase.Result.NoAreasSet ->
                    AuthenticationResults.NoAreaSet

                RegisterUserUseCase.Result.OK ->
                    AuthenticationResults.OK
            }
            if (resultResponse.equals(AuthenticationResults.OK)) {
                // отправить админу текст какой-то о том что новый пользователь хочет присоединиться.
                val chatId = details.id
                botConfig.adminId?.let { it1 ->
                    sendTextMessage(
                        it1.toChatId(),
                        PersonWantsAdd + details.toString(),
                        replyMarkup = confirmationInlineKeyboard(
                            positiveData = AdminNotice.AdminAnswerYes(chatId),
                            negativeData = AdminNotice.AdminAnswerNo(chatId)
                        )
                    )
                }
            }
            sendTextMessage(it, resultResponse)
        }
    }
    anyState {
        onDataCallbackQuery(AdminNotice.AdminAnswerYes::class) { (data, query) ->
            Users.update({ Users.id eq data.chatId }) {
                it[isApproved] = true
            }
            sendNotificationPreferencesMessage(data.chatId.toChatId())
            state.override { DialogState.Empty }
        }
        onDataCallbackQuery(AdminNotice.AdminAnswerNo::class) { (data, query) ->
            sendTextMessage(data.chatId.toChatId(), AdminDoNotAccept)
        }
    }
}
