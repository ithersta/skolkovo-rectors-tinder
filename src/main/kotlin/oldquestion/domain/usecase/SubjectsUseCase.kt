package oldquestion.domain.usecase

import common.domain.Transaction
import oldquestion.domain.repository.QuestionRepository
import org.koin.core.annotation.Single

@Single
class SubjectsUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long): Map<Long, String> = transaction {
        return@transaction questionRepository.getSubjectsByUserIdAndIsClosed(userId)
    }
}
