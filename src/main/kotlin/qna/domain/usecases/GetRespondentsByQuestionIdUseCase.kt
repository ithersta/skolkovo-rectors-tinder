package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.ResponseRepository

@Single
class GetRespondentsByQuestionIdUseCase(
    private val responseRepository: ResponseRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long, offset: Int, limit: Int): List<Long> = transaction {
        return@transaction responseRepository.getRespondentsByQuestionId(questionId).drop(offset).take(limit)
    }
}
