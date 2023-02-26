package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.UserAreasRepository

@Single
class SubjectsUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long, questionArea: Int): Map<Long, String> = transaction {
        return@transaction userAreasRepository.getSubjectsByChatId(userId, questionArea)
    }
}
