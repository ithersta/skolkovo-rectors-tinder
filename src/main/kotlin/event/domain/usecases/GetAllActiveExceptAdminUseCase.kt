package event.domain.usecases

import auth.domain.repository.UserRepository
import common.domain.Transaction
import config.BotConfig
import org.koin.core.annotation.Single

@Single
class GetAllActiveExceptAdminUseCase(
    private val userRepository: UserRepository,
    private val botConfig: BotConfig,
    private val transaction: Transaction
) {
    operator fun invoke(): List<Long> = transaction {
        return@transaction userRepository.getAllActiveExceptUser(botConfig.adminId)
    }
}
