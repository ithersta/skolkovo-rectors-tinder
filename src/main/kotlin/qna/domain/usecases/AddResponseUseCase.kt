package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.ResponsesRepository

@Single
class AddResponseUseCase (
    private val responsesRepository: ResponsesRepository,
    private val transaction: Transaction
){
    operator fun invoke(questionId: Long, respondentId: Long) = transaction {
        responsesRepository.add(questionId, respondentId)
    }
}