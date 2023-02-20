package qna.domain.usecase

import auth.domain.repository.UserAreasRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class UserAreasUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long): Set<String> = transaction {
        return@transaction userAreasRepository.getSubjectsByChatId(userId)
    }
}
