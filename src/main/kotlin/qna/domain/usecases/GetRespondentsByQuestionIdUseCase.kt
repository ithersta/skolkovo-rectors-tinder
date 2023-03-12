package qna.domain.usecases

import common.domain.Paginated
import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.ResponseRepository

@Single
class GetRespondentsByQuestionIdUseCase(
    private val responseRepository: ResponseRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long, offset: Int, limit: Int): Paginated<Long> = transaction {
        return@transaction responseRepository.getRespondentsByQuestionId(questionId, offset, limit)
    }
}
