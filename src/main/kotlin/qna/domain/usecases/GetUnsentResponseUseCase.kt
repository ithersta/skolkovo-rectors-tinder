package qna.domain.usecases

import auth.domain.entities.User
import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.Response
import qna.domain.repository.ResponseRepository

@Single
class GetUnsentResponseUseCase(
    private val transaction: Transaction,
    private val responseRepository: ResponseRepository,
    private val getQuestionByIdUseCase: GetQuestionByIdUseCase,
    private val getUserDetailsUseCase: GetUserDetailsUseCase
) {
    sealed interface Result {
        object Unauthorized : Result
        object NoSuchQuestion : Result
        object NoMoreResponses : Result
        data class OK(val response: Response, val respondent: User.Details) : Result
    }

    operator fun invoke(fromUserId: Long, questionId: Long) = transaction {
        val question = getQuestionByIdUseCase(questionId) ?: return@transaction Result.NoSuchQuestion
        if (question.authorId != fromUserId) return@transaction Result.Unauthorized
        val response = responseRepository.getAnyUnsent(questionId) ?: return@transaction Result.NoMoreResponses
        val respondent = getUserDetailsUseCase(response.respondentId)!!
        Result.OK(response, respondent).also {
            responseRepository.markAsSent(response.id)
        }
    }
}
