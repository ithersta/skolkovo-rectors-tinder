package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.BaseStatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import com.ithersta.tgbotapi.pagination.replyMarkup
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendContact
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import org.koin.core.component.inject
import qna.domain.usecases.GetClosedQuestionsUseCase
import qna.domain.usecases.GetRespondentUseCase
import qna.domain.usecases.GetUserDetailsUseCase
import qna.telegram.queries.SelectOldQuestionRespondent
import qna.telegram.queries.SelectTopic
import qna.telegram.strings.Strings
import qna.telegram.strings.Strings.OldQuestion.HaveNotOldQuestion
import qna.telegram.strings.Strings.OldQuestion.ListClosedQuestions
import qna.telegram.strings.Strings.OldQuestion.ListOfRespondents

private lateinit var oldQuestionsPager: InlineKeyboardPager<Unit, DialogState, User, User.Normal>

suspend fun BaseStatefulContext<DialogState, User, *, out User.Normal>.sendOldQuestionsPager(
    chatIdentifier: IdChatIdentifier
) {
    val replyMarkup = oldQuestionsPager.replyMarkup
    if (replyMarkup.keyboard.isNotEmpty()) {
        sendTextMessage(chatIdentifier, ListClosedQuestions, replyMarkup = replyMarkup)
    } else {
        sendTextMessage(chatIdentifier, HaveNotOldQuestion)
    }
}

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.oldQuestionFlow() {
    val getClosedQuestions: GetClosedQuestionsUseCase by inject()
    val getAuthor: GetRespondentUseCase by inject()
    val getUserById: GetUserDetailsUseCase by inject()
    oldQuestionsPager = pager(id = "old_questions_pager") {
        val subjects = getClosedQuestions.invoke(context!!.user.id)
        val paginatedSubjects = subjects.drop(offset).take(limit)
        inlineKeyboard {
            paginatedSubjects.forEach { item ->
                row {
                    dataButton(item.subject.value, SelectTopic(item.id!!))
                }
            }
            navigationRow(itemCount = subjects.size)
        }
    }
    anyState {
        onDataCallbackQuery(SelectTopic::class) { (data, query) ->
            val list = getAuthor.invoke(data.questionId)
            if (list.isNotEmpty()) {
                sendTextMessage(
                    query.user.id,
                    ListOfRespondents,
                    replyMarkup = inlineKeyboard {
                        getAuthor.invoke(data.questionId).forEach { item ->
                            row {
                                dataButton(
                                    item.name.value,
                                    SelectOldQuestionRespondent(item.id)
                                )
                            }
                        }
                    }
                )
                answer(query)
            } else {
                sendTextMessage(query.user.id, Strings.RespondentsNoAnswer.NoRespondent)
                state.override { DialogState.Empty }
            }
        }
        onDataCallbackQuery(SelectOldQuestionRespondent::class) { (data, query) ->
            val user = getUserById(data.responseId)
            if (user != null) {
                sendContact(query.user.id, phoneNumber = user.phoneNumber.toString(), firstName = user.name.value)
            }
            answer(query)
        }
    }
}
