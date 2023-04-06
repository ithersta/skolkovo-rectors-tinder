package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class GetUserRoleUseCase(
    private val userRepository: UserRepository,
    private val isAdmin: IsAdminUseCase,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long): User = transaction {
        when {
            userRepository.isRegistered(id).not() -> User.Unauthenticated
            userRepository.get(id)?.isApproved == false -> User.Unapproved
            isAdmin(id) -> User.Admin(id)
            else -> User.Normal(id)
        }
    }
}
