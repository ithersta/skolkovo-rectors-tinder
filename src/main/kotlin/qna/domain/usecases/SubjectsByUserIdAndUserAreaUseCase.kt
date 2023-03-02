package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class SubjectsByUserIdAndUserAreaUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long, userArea: QuestionArea): Map<Long, String> = transaction {
        return@transaction userAreasRepository.getSubjectsByUserId(userId, userArea)
    }
}
