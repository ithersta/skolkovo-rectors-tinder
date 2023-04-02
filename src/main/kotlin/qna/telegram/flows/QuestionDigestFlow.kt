package qna.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.StatefulContext
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import common.telegram.DialogState
import common.telegram.strings.CommonStrings
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContentOrThrow
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.inline.dataInlineButton
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.row
import generated.RoleFilterBuilder
import generated.dataButton
import generated.onDataCallbackQuery
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantComponentSerializer
import kotlinx.serialization.Serializable
import notifications.domain.entities.NotificationPreference
import notifications.domain.usecases.GetQuestionsDigestUseCase
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import qna.domain.entities.QuestionArea
import qna.domain.usecases.AddResponseUseCase
import qna.domain.usecases.GetQuestionByIdUseCase
import qna.domain.usecases.GetUserDetailsUseCase
import qna.domain.usecases.HasResponseUseCase
import qna.telegram.queries.QuestionDigestQuery
import qna.telegram.strings.ButtonStrings
import qna.telegram.strings.Strings

lateinit var questionDigestPager: InlineKeyboardPager<QuestionDigestPagerData, DialogState, User, User.Normal>

@Serializable
class QuestionDigestPagerData(
    val userId: Long,
    @Serializable(with = InstantComponentSerializer::class)
    val from: Instant,
    @Serializable(with = InstantComponentSerializer::class)
    val until: Instant,
    val area: QuestionArea?,
    val notificationPreference: NotificationPreference?
)

fun RoleFilterBuilder<User.Normal>.questionDigestFlow() {
    val getQuestionsDigest: GetQuestionsDigestUseCase by inject()
    val getQuestionById: GetQuestionByIdUseCase by inject()
    val addResponse: AddResponseUseCase by inject()
    val hasResponse: HasResponseUseCase by inject()
    questionDigestPager = pager(id = "question_digest", dataKClass = QuestionDigestPagerData::class) {
        val questions = getQuestionsDigest(
            from = data.from,
            until = data.until,
            userId = data.userId,
            limit = limit,
            offset = offset,
            area = data.area
        )
        inlineKeyboard {
            if (data.area != null) {
                row { dataButton(CommonStrings.Button.Back, QuestionDigestQuery.BackToAreas) }
                if (questions.count == 0) {
                    row { dataButton(Strings.NoInterestingQuestions, QuestionDigestQuery.BackToAreas) }
                }
            }
            questions.slice.forEach { question ->
                row {
                    dataButton(
                        text = buildString {
                            if (hasResponse(data.userId, question.id!!)) append("✅ ")
                            append(question.subject)
                        },
                        data = QuestionDigestQuery.SelectQuestion(question.id!!, page, data)
                    )
                }
            }
            navigationRow(questions.count)
        }
    }
    anyState {
        onDataCallbackQuery(QuestionDigestQuery.BackToAreas::class) { (_, query) ->
            val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
            edit(message, Strings.QuestionAreasList, replyMarkup = questionDigestAreasKeyboard(query.from.id))
            answer(query)
        }
        onDataCallbackQuery(QuestionDigestQuery.SelectArea::class) { (data, query) ->
            val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
            edit(
                message,
                Strings.InterestingQuestionsList,
                replyMarkup = questionDigestPager.replyMarkup(
                    QuestionDigestPagerData(
                        userId = query.from.id.chatId,
                        from = Instant.DISTANT_PAST,
                        until = Instant.DISTANT_FUTURE,
                        area = data.area,
                        notificationPreference = null
                    )
                )
            )
            answer(query)
        }
        onDataCallbackQuery(QuestionDigestQuery.SelectQuestion::class) { (data, query) ->
            showQuestion(query, getQuestionById, hasResponse, data)
            answer(query)
        }
        onDataCallbackQuery(QuestionDigestQuery.Respond::class) { (data, query) ->
            addResponse(data.questionId, query.from.id.chatId)
            showQuestion(query, getQuestionById, hasResponse, data)
            answer(query)
        }
        onDataCallbackQuery(QuestionDigestQuery.Back::class) { (data, query) ->
            val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
            edit(
                message,
                data.pagerData.notificationPreference?.let {
                    notifications.telegram.Strings.newQuestionsMessage(it)
                } ?: Strings.InterestingQuestionsList,
                replyMarkup = questionDigestPager.page(data.pagerData, data.page)
            )
            answer(query)
        }
    }
}

suspend fun TelegramBot.sendQuestionDigestAreas(chatIdentifier: IdChatIdentifier) {
    send(chatIdentifier, Strings.QuestionAreasList, replyMarkup = questionDigestAreasKeyboard(chatIdentifier))
}

private fun questionDigestAreasKeyboard(chatIdentifier: IdChatIdentifier): InlineKeyboardMarkup {
    val getUserDetails: GetUserDetailsUseCase by GlobalContext.get().inject()
    val userDetails = getUserDetails(chatIdentifier.chatId)!!
    return inlineKeyboard {
        userDetails.areas.forEach {
            row {
                dataButton(auth.telegram.Strings.questionAreaToString.getValue(it), QuestionDigestQuery.SelectArea(it))
            }
        }
    }
}

private suspend fun StatefulContext<DialogState, User, DialogState, User.Normal>.showQuestion(
    query: DataCallbackQuery,
    getQuestionById: GetQuestionByIdUseCase,
    hasResponseUseCase: HasResponseUseCase,
    data: QuestionDigestQuery.ShowQuestionQuery
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
                    data = QuestionDigestQuery.Back(data.returnToPage, data.pagerData)
                )
                if (hasResponse.not() && question.isClosed.not()) {
                    dataButton(
                        text = ButtonStrings.Respond,
                        data = QuestionDigestQuery.Respond(data.questionId, data.returnToPage, data.pagerData)
                    )
                }
            }
        }
    )
}
