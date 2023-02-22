package qna.domain.usecase

import auth.domain.repository.UserAreasRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class SubjectsUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long, offset: Long, limit: Int): Map<Long, String> = transaction {
        return@transaction userAreasRepository.getSubjectsByChatId(userId, offset, limit)
    }
}
