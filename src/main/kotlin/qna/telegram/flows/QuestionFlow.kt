package qna.telegram.flows

import auth.domain.entities.User
import auth.telegram.Strings
import auth.telegram.queries.SelectArea
import auth.telegram.queries.SelectSubject
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.pagination.pager
import common.telegram.DialogState
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import generated.dataButton
import generated.onDataCallbackQuery
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import qna.domain.usecases.GetQuestionByIdUseCase
import qna.domain.usecases.GetQuestionsByUserIdAndUserAreaUseCase
import qna.domain.usecases.GetUserDetailsUseCase
import qna.telegram.strings.Strings.TargetArea.HaveNotQuestionInThisArea
import qna.telegram.strings.Strings.TargetArea.ListQuestion
import qna.telegram.strings.Strings.TargetArea.ListSpheres

suspend fun TelegramBot.sendInterestingQuestionAreas(chatIdentifier: IdChatIdentifier) {
    val getUserDetails: GetUserDetailsUseCase by GlobalContext.get().inject()
    sendTextMessage(
        chatIdentifier,
        ListSpheres,
        replyMarkup = inlineKeyboard {
            getUserDetails(chatIdentifier.chatId)!!.areas.forEach { area ->
                val areaToString = Strings.questionAreaToString[area]
                row {
                    dataButton(areaToString!!, SelectArea(area))
                }
            }
        }
    )
}

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.questionFlow() {
    val questionsByUserIdAndUserAreaUseCase: GetQuestionsByUserIdAndUserAreaUseCase by inject()
    val getQuestionByIdUseCase: GetQuestionByIdUseCase by inject()
    val interestingQuestionsPager = pager(id = "interesting_questions", dataKClass = SelectArea::class) {
        val questions = questionsByUserIdAndUserAreaUseCase(context!!.user.id, data.area)
        val paginatedSubjects = questions.drop(offset).take(limit)
        inlineKeyboard {
            paginatedSubjects.forEach { item ->
                row {
                    dataButton(item.subject, SelectSubject(item.id!!))
                }
            }
            navigationRow(itemCount = questions.size)
        }
    }
    anyState {
        onDataCallbackQuery(SelectArea::class) { (data, query) ->
            val replyMarkup = interestingQuestionsPager.replyMarkup(data)
            if (replyMarkup.keyboard.isEmpty()) {
                sendTextMessage(query.user.id, HaveNotQuestionInThisArea)
                state.override { DialogState.Empty }
            } else {
                sendTextMessage(query.user.id, ListQuestion, replyMarkup = replyMarkup)
            }
            answer(query)
        }
        onDataCallbackQuery(SelectSubject::class) { (data, query) ->
            sendQuestionMessage(query.user.id, getQuestionByIdUseCase(data.questionId)!!)
            answer(query)
        }
    }
}
