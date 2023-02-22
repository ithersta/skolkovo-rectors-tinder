package qna.domain.usecase

import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class GetFirstNameUseCase(
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long): String = transaction {
        return@transaction userRepository.getFirstName(id)
    }
}