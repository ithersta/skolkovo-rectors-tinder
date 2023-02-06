package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.UserRepository
import config.BotConfig
import org.koin.core.annotation.Single

@Single
class GetUserUseCase(
    private val userRepository: UserRepository,
    private val botConfig: BotConfig
) {
    operator fun invoke(id: Long): User {
        val details = userRepository.get(id)
        return when {
            botConfig.adminId == id -> User.Admin
            details != null -> User.Normal(details)
            else -> User.Unauthenticated
        }
    }
}
