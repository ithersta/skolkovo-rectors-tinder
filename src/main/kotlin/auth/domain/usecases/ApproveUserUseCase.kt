package auth.domain.usecases

import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class ApproveUserUseCase(
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long) = transaction {
        userRepository.approve(userId)
    }
}