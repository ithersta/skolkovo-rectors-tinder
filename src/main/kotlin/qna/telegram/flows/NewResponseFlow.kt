package qna.telegram.flows

import auth.domain.entities.User
import common.telegram.functions.deleteAfterDelay
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.api.send.sendContact
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.withContentOrThrow
import dev.inmo.tgbotapi.types.message.content.TextContent
import generated.RoleFilterBuilder
import generated.dataButton
import generated.onDataCallbackQuery
import org.koin.core.component.inject
import qna.domain.usecases.AddAcceptedResponseUseCase
import qna.domain.usecases.GetQuestionByIdUseCase
import qna.domain.usecases.GetUnsentResponseUseCase
import qna.telegram.queries.NewResponsesQuery.Accept
import qna.telegram.queries.NewResponsesQuery.SeeNew
import qna.telegram.strings.Strings

fun RoleFilterBuilder<User.Normal>.newResponseFlow() {
    val getUnsentResponse: GetUnsentResponseUseCase by inject()
    val addAcceptedResponse: AddAcceptedResponseUseCase by inject()
    val getQuestionById: GetQuestionByIdUseCase by inject()
    anyState {
        onDataCallbackQuery(SeeNew::class) { (data, query) ->
            val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
            when (val result = getUnsentResponse(query.from.id.chatId, data.questionId)) {
                is GetUnsentResponseUseCase.Result.OK -> {
                    val keyboard = flatInlineKeyboard {
                        dataButton(Strings.NewResponses.AcceptButton, Accept(result.response.id, data.questionId))
                        dataButton(Strings.NewResponses.NextButton, SeeNew(data.questionId))
                    }
                    val text = Strings.NewResponses.profile(result.respondent)
                    if (data.doEdit) {
                        edit(message, text, replyMarkup = keyboard)
                    } else {
                        edit(message, replyMarkup = null)
                        send(query.from, text, replyMarkup = keyboard)
                    }
                }

                GetUnsentResponseUseCase.Result.NoMoreResponses -> {
                    val ephemeralMessage = if (data.doEdit) {
                        edit(message, text = Strings.NewResponses.NoMoreResponses, replyMarkup = null)
                    } else {
                        edit(message, replyMarkup = null)
                        send(message.chat, text = Strings.NewResponses.NoMoreResponses)
                    }
                    deleteAfterDelay(ephemeralMessage)
                    send(message.chat, Strings.NewResponses.WriteToCompanion)
                    getQuestionById(data.questionId)?.let { question ->
                        send(message.chat, Strings.NewResponses.CopyQuestion)
                        send(message.chat, question.text)
                    }
                }

                else -> return@onDataCallbackQuery
            }
            answer(query)
        }
        onDataCallbackQuery(Accept::class) { (data, query) ->
            val result = addAcceptedResponse(query.from.id.chatId, data.responseId)
            check(result is AddAcceptedResponseUseCase.Result.OK)
            val message = query.messageCallbackQueryOrThrow().message.withContentOrThrow<TextContent>()
            val keyboard = flatInlineKeyboard {
                dataButton(Strings.NewResponses.NextButton, SeeNew(data.questionId, doEdit = false))
            }
            edit(message, Strings.NewResponses.acceptedProfile(result.respondent), replyMarkup = keyboard)
            sendContact(
                chat = query.user,
                phoneNumber = result.respondent.phoneNumber.value,
                firstName = result.respondent.name,
            )
            answer(query)
        }
    }
}
