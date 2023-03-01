package qna.telegram

import common.telegram.confirmationInlineKeyboard
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import qna.domain.usecases.AddResponseUseCase
import qna.domain.usecases.GetQuestionByIdUseCase
import qna.domain.usecases.GetUserDetailsUseCase
import qna.telegram.queries.AcceptUserQuery
import qna.telegram.queries.DeclineUserQuery
import qna.telegram.strings.Strings

@Single
class NewResponsesSender(
    private val addResponseUseCase: AddResponseUseCase,
    private val getQuestionById: GetQuestionByIdUseCase,
    private val getUserDetails: GetUserDetailsUseCase
) {
    fun BehaviourContext.setup() = launch {
        addResponseUseCase.newResponses.collect { response ->
            runCatching {
                val question = getQuestionById(response.questionId)!!
                val authorId = question.authorId
                val respondent = getUserDetails(response.respondentId)
                if (respondent != null) {
                    sendTextMessage(
                        authorId.toChatId(),
                        Strings.ToAskUser.message(
                            respondent.name,
                            respondent.city,
                            respondent.job,
                            respondent.organization,
                            respondent.activityDescription
                        ),
                        replyMarkup = confirmationInlineKeyboard(
                            positiveData = AcceptUserQuery(respondent.id, response.questionId, response.id),
                            negativeData = DeclineUserQuery(respondent.id)
                        )
                    )
                }
            }
        }
    }
}
