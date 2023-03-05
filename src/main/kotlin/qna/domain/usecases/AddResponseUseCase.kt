package qna.domain.usecases

import common.domain.Transaction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.annotation.Single
import qna.domain.entities.Response
import qna.domain.repository.ResponseRepository

@Single
class AddResponseUseCase(
    private val responseRepository: ResponseRepository,
    private val getQuestionById: GetQuestionByIdUseCase,
    private val transaction: Transaction
) {
    sealed interface Result {
        object NoSuchQuestion : Result
        object QuestionClosed : Result
        object AlreadyExists : Result
        data class OK(val response: Response) : Result
    }

    private val _newResponses = Channel<Response>(BUFFERED)
    val newResponses = _newResponses.receiveAsFlow()

    suspend operator fun invoke(questionId: Long, respondentId: Long): Result {
        val result = transaction {
            val question = getQuestionById(questionId) ?: return@transaction Result.NoSuchQuestion
            if (question.isClosed) return@transaction Result.QuestionClosed
            val id = responseRepository.add(questionId, respondentId)
            Result.OK(Response(id, questionId, respondentId))
        }
        (result as? Result.OK)?.let { _newResponses.send(it.response) }
        return result
    }
}
