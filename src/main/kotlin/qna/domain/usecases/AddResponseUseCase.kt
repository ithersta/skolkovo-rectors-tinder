package qna.domain.usecases

import common.domain.Transaction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
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
        data class OK(val response: Response) : Result
    }

    class NewResponseMessage(val response: Response, val responseCount: Int)

    private val _newResponses = Channel<NewResponseMessage>(BUFFERED)
    val newResponses = _newResponses.receiveAsFlow()

    suspend operator fun invoke(questionId: Long, respondentId: Long) = transaction {
        val question = getQuestionById(questionId) ?: return@transaction Result.NoSuchQuestion
        if (question.isClosed) return@transaction Result.QuestionClosed
        val id = responseRepository.add(questionId, respondentId)
        val response = Response(id, questionId, respondentId)
        val responseCount = responseRepository.count(questionId)
        runBlocking {
            _newResponses.send(NewResponseMessage(response, responseCount))
        }
        Result.OK(response)
    }
}
