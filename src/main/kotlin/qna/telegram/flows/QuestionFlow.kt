package qna.telegram.flows

import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.queries.AnswerUser
import auth.telegram.queries.SelectArea
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.BaseStatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.pagination.pager
import common.telegram.DialogState
import common.telegram.strings.CommonStrings.Button.No
import common.telegram.strings.CommonStrings.Button.Yes
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import menus.states.MenuState
import org.koin.core.component.inject
import qna.domain.entities.Question
import qna.domain.usecases.GetQuestionByIdUseCase
import qna.domain.usecases.GetUserDetailsUseCase
import qna.domain.usecases.SubjectsUseCase
import qna.telegram.queries.AcceptQuestionQuery
import qna.telegram.queries.DeclineQuestionQuery
import qna.telegram.strings.Strings.TargetArea.ListQuestion
import qna.telegram.strings.Strings.TargetArea.buildQuestionByQuestionText
import qna.telegram.strings.Strings.TargetArea.haveNotQuestionInThisArea
import qna.telegram.strings.Strings.TargetArea.listSpheres

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.feedbackFlow() {
    val subjectsByChatId: SubjectsUseCase by inject()
    val getQuestionByIdUseCase: GetQuestionByIdUseCase by inject()
    val getUserDetailsUseCase: GetUserDetailsUseCase by inject()
    val answerForUser: List<String> = listOf(Yes, No)
    val subjectsPager =
        pager(id = "subjects", dataKClass = SelectArea::class) {
            val subjects = subjectsByChatId.invoke(context!!.user.id, data.area).toList()
            val paginatedNumbers = subjects.drop(offset).take(limit)
            inlineKeyboard {
                paginatedNumbers.forEach { item ->
                    row {
                        dataButton(item.second, SelectSubject(item.first))
                    }
                }
                navigationRow(itemCount = subjects.size)
            }
        }
    state<MenuState.CurrentIssues> {
        onEnter {
            sendTextMessage(
                it.chatId.toChatId(),
                listSpheres,
                replyMarkup = inlineKeyboard {
                    getUserDetailsUseCase.invoke(it.chatId)!!.areas.forEach { area ->
                        val areaToString = Strings.questionAreaToString[area]
                        row {
                            dataButton(areaToString!!, SelectArea(area))
                        }
                    }
                }
            )
        }

        onDataCallbackQuery(SelectArea::class) { (data, query) ->
            val replyMarkup = subjectsPager.replyMarkup(
                data,
                this as BaseStatefulContext<DialogState, User, DialogState, User.Normal>
            )
            if (replyMarkup.keyboard.isEmpty()) {
                sendTextMessage(query.user.id, haveNotQuestionInThisArea)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(query.user.id, ListQuestion, replyMarkup = replyMarkup)
            }
            answer(query)
        }
    }
    anyState {
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            sendTextMessage(
                query.user.id,
                buildQuestionByQuestionText(getQuestionByIdUseCase.invoke(data.questionId)!!.text),
                replyMarkup = inlineKeyboard {
                    answerForUser.forEach {
                        row {
                            dataButton(it, AnswerUser(data.questionId, it))
                        }
                    }
                }
            )
            answer(query)
        }
        onDataCallbackQuery(AnswerUser::class) { (data, query) ->
            if (data.answer == Yes) {
                val question: Question = getQuestionByIdUseCase(data.questionId)!!
                sendQMessage(question.authorId.toChatId(), question)
            }
            state.override { DialogState.Empty }
            answer(query)
        }
    }
}

suspend fun TelegramBot.sendQMessage(chatId: ChatId, question: Question) = sendTextMessage(
    chatId,
    qna.telegram.strings.Strings.ToAnswerUser.message(question.subject, question.text),
    replyMarkup = inlineKeyboard {
        row {
            checkNotNull(question.id)
            dataButton(Yes, AcceptQuestionQuery(question.id))
        }
        row {
            dataButton(No, DeclineQuestionQuery)
        }
    }
)
