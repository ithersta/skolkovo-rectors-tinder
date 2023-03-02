package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.QuestionRepository


@Single
class SubjectsUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long): Map<Long, String> = transaction {
        return@transaction questionRepository.getSubjectsByUserIdAndIsClosed(userId)
    }
}
