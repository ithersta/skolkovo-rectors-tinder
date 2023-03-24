package qna.telegram.flows

import auth.domain.entities.User
import auth.telegram.Strings.OldQuestion.HaveNotOldQuestion
import auth.telegram.Strings.OldQuestion.ListClosedQuestions
import auth.telegram.Strings.OldQuestion.ListOfRespondents
import auth.telegram.queries.SelectRespondent
import auth.telegram.queries.SelectTopic
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
import qna.domain.usecases.GetAuthorUseCase
import qna.domain.usecases.GetClosedQuestionsUseCase
import qna.telegram.strings.Strings

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
    val getAuthor: GetAuthorUseCase by inject()
    oldQuestionsPager = pager(id = "old_questions_pager") {
        val subjects = getClosedQuestions.invoke(context!!.user.id)
        val paginatedSubjects = subjects.drop(offset).take(limit)
        inlineKeyboard {
            paginatedSubjects.forEach { item ->
                row {
                    dataButton(item.subject, SelectTopic(item.id!!))
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
                                    item.name,
                                    SelectRespondent(name = item.name, phoneNumber = item.phoneNumber.toString())
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
        onDataCallbackQuery(SelectRespondent::class) { (data, query) ->
            sendContact(query.user.id, phoneNumber = data.phoneNumber, firstName = data.name)
            answer(query)
        }
    }
}
