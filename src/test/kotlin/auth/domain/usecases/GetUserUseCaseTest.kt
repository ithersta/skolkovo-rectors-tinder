package auth.domain.usecases

import NoOpTransaction
import auth.domain.entities.User
import auth.domain.repository.UserRepository
import config.BotConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class GetUserUseCaseTest {
    @Test
    fun `All roles`() {
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(0L) } returns false
        every { userRepository.isRegistered(1L) } returns false
        every { userRepository.isRegistered(2L) } returns true
        val botConfig = BotConfig(0L, 0L)
        val getUser = GetUserUseCase(userRepository, botConfig, NoOpTransaction)
        assertTrue { getUser(0L) == User.Admin }
        assertTrue { getUser(1L) == User.Unauthenticated }
        assertTrue { getUser(2L) == User.Normal }
    }
}
