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
    private val transaction: Transaction
) {
    private val _newResponses = Channel<Response>(BUFFERED)
    val newResponses = _newResponses.receiveAsFlow()

    suspend operator fun invoke(questionId: Long, respondentId: Long) {
        val id = transaction { responseRepository.add(questionId, respondentId) }
        _newResponses.send(Response(id, questionId, respondentId))
    }
}
