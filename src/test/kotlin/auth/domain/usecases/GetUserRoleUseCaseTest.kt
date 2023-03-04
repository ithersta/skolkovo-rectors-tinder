package auth.domain.usecases

import NoOpTransaction
import auth.domain.entities.User
import auth.domain.repository.UserRepository
import config.BotConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class GetUserRoleUseCaseTest {
    @Test
    fun `All roles`() {
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(0L) } returns true
        every { userRepository.isRegistered(1L) } returns false
        every { userRepository.isRegistered(2L) } returns true
        val botConfig = BotConfig(0L, 0L)
        val getUser = GetUserRoleUseCase(userRepository, botConfig, NoOpTransaction)
        assertTrue { getUser(0L) == User.Admin(1) }
        assertTrue { getUser(1L) == User.Unauthenticated }
        assertTrue { getUser(2L) is User.Normal }
    }
}
