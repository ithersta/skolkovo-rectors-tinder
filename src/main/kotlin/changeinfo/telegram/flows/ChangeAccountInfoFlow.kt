package changeinfo.telegram.flows

import auth.domain.entities.User
import changeinfo.domain.interactors.ChangeAccountInfoInteractor
import changeinfo.telegram.Strings
import changeinfo.telegram.queries.*
import changeinfo.telegram.states.*
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import common.telegram.functions.chooseOrganizationType
import common.telegram.functions.chooseQuestionAreas
import common.telegram.functions.selectCity
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import generated.onDataCallbackQuery
import org.koin.core.component.inject
import qna.domain.usecases.GetUserDetailsUseCase

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.changeAccountInfoFlow() {
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val changeAccountInfoInteractor: ChangeAccountInfoInteractor by inject()
    anyState {
        onDataCallbackQuery(WaitingForCity::class) {
            state.override { WaitingForCityState() }
        }
        onDataCallbackQuery(WaitingForNewName::class) {
            state.override { WaitingForNewNameState }
        }
        onDataCallbackQuery(WaitingForProfession::class) {
            state.override { WaitingForProfessionState }
        }
        onDataCallbackQuery(WaitingForOrganization::class) {
            state.override { WaitingForOrganizationTypeState }
        }
        onDataCallbackQuery(WaitingForProfessionalDescription::class) {
            state.override { WaitingForProfessionalDescriptionState }
        }
        onDataCallbackQuery(WaitingForQuestionAreas::class) { (_, query) ->
            state.override { WaitingForQuestionAreasState(getUserDetailsUseCase(query.from.id.chatId)!!.areas) }
        }
        onDataCallbackQuery(BackToMain::class) { (_, query) ->
            state.override { DialogState.Empty }
            answer(query)
        }
    }
    state<WaitingForCityState> {
        selectCity(
            onFinish = { _, city -> ChangeCityState(city) }
        )
    }
    state<ChangeCityState> {
        onEnter {
            changeAccountInfoInteractor.changeCity(it.chatId, state.snapshot.city)
            state.override { DialogState.Empty }
        }
    }
    state<WaitingForNewNameState> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Fields.Name.Message
            )
        }
        onText {
            changeAccountInfoInteractor.changeName(it.chat.id.chatId, it.content.text)
            state.override { DialogState.Empty }
        }
    }
    state<WaitingForProfessionState> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Fields.Job.Message
            )
        }
        onText {
            changeAccountInfoInteractor.changeJob(it.chat.id.chatId, it.content.text)
            state.override { DialogState.Empty }
        }
    }

    state<WaitingForOrganizationTypeState> {
        chooseOrganizationType(
            text = Strings.Fields.Organization.Type,
            onFinish = { _, type -> ChangeOrganizationTypeState(type) }
        )
    }
    state<ChangeOrganizationTypeState> {
        onEnter {
            changeAccountInfoInteractor.changeOrganizationType(it.chatId, state.snapshot.type)
            state.override { WaitingForOrganizationState }
        }
    }

    state<WaitingForOrganizationState> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Fields.Organization.Message
            )
        }
        onText {
            changeAccountInfoInteractor.changeOrganization(it.chat.id.chatId, it.content.text)
            state.override { DialogState.Empty }
        }
    }

    state<WaitingForProfessionalDescriptionState> {
        onEnter {
            sendTextMessage(
                it,
                Strings.Fields.ActivityDescription.Message
            )
        }
        onText {
            changeAccountInfoInteractor.changeActivityDescription(it.chat.id.chatId, it.content.text)
            state.override { DialogState.Empty }
        }
    }
    state<WaitingForQuestionAreasState> {
        chooseQuestionAreas(
            text = Strings.Fields.Areas,
            getAreas = { it.questionAreas },
            getMessageId = { it.messageId },
            onSelectionChanged = { state, questionAreas -> state.copy(questionAreas = questionAreas) },
            onMessageIdSet = { state, messageId -> state.copy(messageId = messageId) },
            onFinish = { ChangeQuestionAreaState(it.questionAreas) }

        )
    }
    state<ChangeQuestionAreaState> {
        onEnter {
            changeAccountInfoInteractor.changeAreas(it.chatId, state.snapshot.questionAreas)
            state.override { DialogState.Empty }
        }
    }
}
