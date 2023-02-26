package notifications.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.StatefulContext
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import common.telegram.CommonStrings
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContentOrThrow
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.row
import generated.RoleFilterBuilder
import generated.dataButton
import generated.onDataCallbackQuery
import kotlinx.coroutines.launch
import notifications.domain.entities.NewQuestionsNotification
import notifications.domain.usecases.GetNewQuestionsNotificationFlowUseCase
import notifications.domain.usecases.GetQuestionsDigestUseCase
import notifications.telegram.Strings
import notifications.telegram.queries.NewQuestionsNotificationQuery
import org.koin.core.component.inject
import qna.domain.usecases.AddResponseUseCase
import qna.domain.usecases.GetQuestionByIdUseCase
import qna.domain.usecases.HasResponseUseCase

lateinit var newQuestionsPager: InlineKeyboardPager<NewQuestionsNotification>

fun RoleFilterBuilder<User.Normal>.newQuestionsNotificationFlow() {
    val getQuestionsDigest: GetQuestionsDigestUseCase by inject()
    val getQuestionById: GetQuestionByIdUseCase by inject()
    val addResponse: AddResponseUseCase by inject()
    val hasResponse: HasResponseUseCase by inject()
    newQuestionsPager = pager(id = "new_questions", dataKClass = NewQuestionsNotification::class) {
        val questions = getQuestionsDigest(
            from = data.from,
            until = data.until,
            userId = data.userId,
            limit = limit,
            offset = offset
        )
        inlineKeyboard {
            questions.slice.forEach { question ->
                row {
                    dataButton(
                        text = question.subject,
                        data = NewQuestionsNotificationQuery.SelectQuestion(question.id!!, page, data)
                    )
                }
            }
            navigationRow(questions.count)
        }
    }
    anyState {
        onDataCallbackQuery(NewQuestionsNotificationQuery.SelectQuestion::class) { (data, query) ->
            showQuestion(query, getQuestionById, hasResponse, data)
            answer(query)
        }
        onDataCallbackQuery(NewQuestionsNotificationQuery.Respond::class) { (data, query) ->
            addResponse(data.questionId, query.from.id.chatId)
            showQuestion(query, getQuestionById, hasResponse, data)
            answer(query)
        }
        onDataCallbackQuery(NewQuestionsNotificationQuery.Back::class) { (data, query) ->
            val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
            edit(
                message,
                Strings.newQuestionsMessage(data.newQuestionsNotification.notificationPreference),
                replyMarkup = newQuestionsPager.page(data.newQuestionsNotification, data.page)
            )
            answer(query)
        }
    }
}

private suspend fun StatefulContext<DialogState, User, DialogState, User.Normal>.showQuestion(
    query: DataCallbackQuery,
    getQuestionById: GetQuestionByIdUseCase,
    hasResponseUseCase: HasResponseUseCase,
    data: NewQuestionsNotificationQuery.ShowQuestionQuery
) {
    val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
    val question = getQuestionById(data.questionId)!!
    val hasResponse = hasResponseUseCase(query.from.id.chatId, data.questionId)
    edit(
        message = message,
        entities = if (hasResponse) Strings.respondedQuestion(question) else Strings.question(question),
        replyMarkup = inlineKeyboard {
            row {
                dataButton(
                    text = CommonStrings.Button.Back,
                    data = NewQuestionsNotificationQuery.Back(data.returnToPage, data.newQuestionsNotification)
                )
                if (hasResponse.not() && question.isClosed.not()) {
                    dataButton(
                        text = Strings.Buttons.Respond,
                        data = NewQuestionsNotificationQuery.Respond(
                            data.questionId,
                            data.returnToPage,
                            data.newQuestionsNotification
                        )
                    )
                }
            }
        }
    )
}
