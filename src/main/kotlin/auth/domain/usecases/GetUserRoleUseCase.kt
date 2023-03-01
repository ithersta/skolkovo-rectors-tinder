package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.UserRepository
import common.domain.Transaction
import config.BotConfig
import org.koin.core.annotation.Single

@Single
class GetUserRoleUseCase(
    private val userRepository: UserRepository,
    private val botConfig: BotConfig,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long): User = transaction {
        when {
            userRepository.isRegistered(id).not() -> User.Unauthenticated
            botConfig.adminId == id -> User.Admin
            else -> User.Normal()
        }
    }
}
