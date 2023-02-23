package qna.domain.usecases

import common.domain.Transaction
import kotlinx.datetime.Instant
import org.koin.core.annotation.Single
import qna.domain.repository.AcceptedResponsesRepository

@Single
class AddAcceptedResponseRepoUseCase(
    private val acceptedResponsesRepository: AcceptedResponsesRepository,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long, time: Instant) = transaction {
        return@transaction acceptedResponsesRepository.add(id, time)
    }
}
