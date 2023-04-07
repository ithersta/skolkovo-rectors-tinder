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
        val userDetails = userRepository.get(id)
        when {
            userDetails == null -> User.Unauthenticated
            isAdmin(id) -> User.Admin(id)
            userDetails.isApproved -> User.Normal(id)
            else -> User.Unapproved
        }
    }
}
