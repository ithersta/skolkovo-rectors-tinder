package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.ResponseRepository

@Single
class HasResponseUseCase(
    private val responseRepository: ResponseRepository,
    private val transaction: Transaction
) {
    operator fun invoke(respondentId: Long, questionId: Long) = transaction {
        responseRepository.has(respondentId, questionId)
    }
}
