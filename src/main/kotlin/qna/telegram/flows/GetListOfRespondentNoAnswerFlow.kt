package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import com.ithersta.tgbotapi.pagination.replyMarkup
import common.telegram.DialogState
import common.telegram.functions.confirmationInlineKeyboard
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.api.send.sendContact
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContentOrThrow
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import feedback.domain.usecases.CloseQuestionUseCase
import generated.dataButton
import generated.onDataCallbackQuery
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.usecases.*
import qna.telegram.queries.*
import qna.telegram.strings.ButtonStrings
import qna.telegram.strings.Strings

lateinit var subjectPager: InlineKeyboardPager<Unit, DialogState, User, User.Normal>
lateinit var respondentPager: InlineKeyboardPager<SeeList, DialogState, User, User.Normal>

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.getListOfRespondentNoAnswerFlow() {
    val getQuestionsByUserIdUseCase: GetQuestionsByUserIdUseCase by inject()
    val closeQuestionUseCase: CloseQuestionUseCase by inject()
    val getRespondentsByQuestionIdUseCase: GetRespondentsByQuestionIdUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val addAcceptedResponse: AddAcceptedResponseUseCase by inject()
    val getRespondentByResponseId: GetRespondentByResponseIdUseCase by inject()

    subjectPager = pager(id = "subjectsNoAnswer") {
        val subject = getQuestionsByUserIdUseCase(context!!.user.id, offset, limit)
        inlineKeyboard {
            subject.slice.forEach { item ->
                row {
                    dataButton(item.subject, SelectSubject(item.authorId, item.id!!))
                }
            }
            navigationRow(itemCount = subject.count)
        }
    }
    respondentPager = pager(id = "respondentNoAnswer", dataKClass = SeeList::class) {
        val respondent = getRespondentsByQuestionIdUseCase(data.questionId, offset, limit)
        inlineKeyboard {
            respondent.slice.forEach { item ->
                val user = getUserDetailsUseCase(item.respondentId)
                row {
                    dataButton(user!!.name, SelectRespondent(item.id))
                }
            }
            navigationRow(itemCount = respondent.count)
        }
    }
    state<MenuState.Questions.GetListOfQuestions> {
        onEnter {
            val replyMarkup = subjectPager.replyMarkup
            if (replyMarkup.keyboard.isEmpty()) {
                sendTextMessage(it, Strings.RespondentsNoAnswer.NoQuestions)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(it, Strings.RespondentsNoAnswer.ListOfSubjects, replyMarkup = replyMarkup)
                state.overrideQuietly { DialogState.Empty }
            }
        }
    }
    anyState {
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            sendTextMessage(
                query.user.id,
                Strings.RespondentsNoAnswer.ChooseAction,
                replyMarkup = inlineKeyboard {
                    row {
                        dataButton(
                            ButtonStrings.RespondentNoAnswer.CloseQuestion,
                            CloseQuestion(data.userId, data.questionId)
                        )
                    }
                    row {
                        dataButton(
                            ButtonStrings.RespondentNoAnswer.SeeList,
                            SeeList(data.userId, data.questionId)
                        )
                    }
                }
            )
            answer(query)
        }
        onDataCallbackQuery(CloseQuestion::class) { (data, query) ->
            closeQuestionUseCase(data.userId, data.questionId)
            sendTextMessage(query.user.id, Strings.RespondentsNoAnswer.CloseQuestionSuccessful)
            state.override { DialogState.Empty }
            answer(query)
        }
        onDataCallbackQuery(SeeList::class) { (data, query) ->
            val replyMarkup = respondentPager.replyMarkup(data)
            if (replyMarkup.keyboard.isEmpty()) {
                sendTextMessage(query.user.id, Strings.RespondentsNoAnswer.NoRespondent)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(
                    query.user.id,
                    Strings.RespondentsNoAnswer.ListOfRespondents,
                    replyMarkup = replyMarkup
                )
            }
            answer(query)
        }
        onDataCallbackQuery(SelectRespondent::class) { (data, query) ->
            val respondent = getRespondentByResponseId(data.responseId)!!
            val keyboard = confirmationInlineKeyboard(
                positiveData = AcceptResponseQuery(data.responseId),
                negativeData = DeclineResponseQuery
            )
            val text = Strings.NewResponses.profile(respondent)
            send(query.from, text, replyMarkup = keyboard)
            answer(query)
        }
        onDataCallbackQuery(DeclineResponseQuery::class) { (_, query) ->
            delete(query.messageCallbackQueryOrThrow().message)
        }
        onDataCallbackQuery(AcceptResponseQuery::class) { (data, query) ->
            val respondent = getRespondentByResponseId(data.responseId)!!
            val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
            addAcceptedResponse(query.user.id.chatId, data.responseId)
            edit(message, Strings.NewResponses.acceptedProfile(respondent), replyMarkup = null)
            sendContact(
                chat = query.user,
                phoneNumber = respondent.phoneNumber.value,
                firstName = respondent.name,
            )
        }
    }
}
