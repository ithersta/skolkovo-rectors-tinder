package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.UserRepository
import common.domain.Transaction
import config.BotConfig
import org.koin.core.annotation.Single

@Single
class GetUserUseCase(
    private val userRepository: UserRepository,
    private val botConfig: BotConfig,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long): User = transaction {
        when {
            botConfig.adminId == id -> User.Admin
            userRepository.isRegistered(id) -> User.Normal
            else -> User.Unauthenticated
        }
    }
}
