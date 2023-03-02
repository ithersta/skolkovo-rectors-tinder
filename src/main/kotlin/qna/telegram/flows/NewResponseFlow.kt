package qna.telegram.flows

import auth.domain.entities.User
import common.telegram.functions.deleteAfterDelay
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContentOrThrow
import dev.inmo.tgbotapi.types.message.content.TextContent
import generated.RoleFilterBuilder
import generated.dataButton
import generated.onDataCallbackQuery
import org.koin.core.component.inject
import qna.domain.usecases.GetUnsentResponseUseCase
import qna.telegram.queries.NewResponsesQuery
import qna.telegram.strings.Strings

fun RoleFilterBuilder<User.Normal>.newResponseFlow() {
    val getUnsentResponse: GetUnsentResponseUseCase by inject()
    anyState {
        onDataCallbackQuery(NewResponsesQuery.SeeNew::class) { (data, query) ->
            val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
            when (val result = getUnsentResponse(query.from.id.chatId, data.questionId)) {
                is GetUnsentResponseUseCase.Result.OK -> {
                    edit(
                        message,
                        entities = Strings.NewResponses.profile(result.respondent),
                        replyMarkup = flatInlineKeyboard {
                            dataButton(Strings.NewResponses.AcceptButton, NewResponsesQuery.Accept(result.response.id))
                            dataButton(Strings.NewResponses.NextButton, NewResponsesQuery.SeeNew(data.questionId))
                        }
                    )
                }

                GetUnsentResponseUseCase.Result.NoMoreResponses -> {
                    edit(message, text = Strings.NewResponses.NoMoreResponses, replyMarkup = null)
                    deleteAfterDelay(message)
                }

                else -> return@onDataCallbackQuery
            }
            answer(query)
        }
        onDataCallbackQuery(NewResponsesQuery.Accept::class) { (data, query) ->
            // TODO
        }
    }
}
