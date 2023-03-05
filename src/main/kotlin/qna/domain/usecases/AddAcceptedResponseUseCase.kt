package qna.domain.usecases

import auth.domain.entities.User
import common.domain.Transaction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.AcceptedResponsesRepository
import qna.domain.repository.QuestionRepository
import qna.domain.repository.ResponseRepository

@Single
class AddAcceptedResponseUseCase(
    private val acceptedResponsesRepository: AcceptedResponsesRepository,
    private val responseRepository: ResponseRepository,
    private val questionRepository: QuestionRepository,
    private val getUserDetails: GetUserDetailsUseCase,
    private val clock: Clock,
    private val transaction: Transaction
) {
    sealed interface Result {
        object NoSuchResponse : Result
        object Unauthorized : Result
        data class OK(val respondent: User.Details) : Result
    }

    class AcceptedResponseMessage(val question: Question, val respondentId: Long)

    private val _newAcceptedResponses = Channel<AcceptedResponseMessage>(Channel.BUFFERED)
    val newAcceptedResponses = _newAcceptedResponses.receiveAsFlow()

    operator fun invoke(fromUserId: Long, id: Long) = transaction {
        val response = responseRepository.get(id) ?: return@transaction Result.NoSuchResponse
        val question = questionRepository.getById(response.questionId) ?: error("Associated question doesn't exist")
        if (question.authorId != fromUserId) return@transaction Result.Unauthorized
        val respondent = getUserDetails(response.respondentId) ?: error("Associated respondent doesn't exist")
        acceptedResponsesRepository.add(id, clock.now())
        runBlocking {
            _newAcceptedResponses.send(AcceptedResponseMessage(question, respondent.id))
        }
        Result.OK(respondent)
    }
}
