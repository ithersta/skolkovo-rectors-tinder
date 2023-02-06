package auth.domain.usecases

import NoOpTransaction
import auth.domain.entities.PhoneNumber
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
        every { userRepository.get(0L) } returns null
        every { userRepository.get(1L) } returns null
        every { userRepository.get(2L) } returns User.Details(
            2L,
            PhoneNumber.of("79000000000")!!,
            "city",
            "job",
            "organization",
            "professionalAreas",
            "activityDescription"
        )
        val botConfig = BotConfig(0L)
        val getUser = GetUserUseCase(userRepository, botConfig, NoOpTransaction)
        assertTrue { getUser(0L) == User.Admin }
        assertTrue { getUser(1L) == User.Unauthenticated }
        assertTrue { getUser(2L) is User.Normal }
    }
}
