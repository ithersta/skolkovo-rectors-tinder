package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea
import qna.domain.repository.QuestionRepository
import qna.domain.repository.UserAreasRepository

@Single
class SubjectsUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long, userArea: QuestionArea): Map<Long, String> = transaction {
        return@transaction userAreasRepository.getSubjectsByUserId(userId, userArea)
    }
}

@Single
class SubjectsUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long): Map<Long, String> = transaction {
        return@transaction questionRepository.getSubjectsByUserIdAndIsClosed(userId)
    }
}