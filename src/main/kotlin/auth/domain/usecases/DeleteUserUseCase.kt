package auth.domain.usecases

import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class DeleteUserUseCase(
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long) = transaction {
        userRepository.delete(id)
    }
}
