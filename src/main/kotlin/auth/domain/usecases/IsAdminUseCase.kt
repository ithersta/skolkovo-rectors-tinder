package auth.domain.usecases

import config.BotConfig
import org.koin.core.annotation.Single

@Single
class IsAdminUseCase(
    private val botConfig: BotConfig
) {
    operator fun invoke(userId: Long) = botConfig.adminId == userId
}
