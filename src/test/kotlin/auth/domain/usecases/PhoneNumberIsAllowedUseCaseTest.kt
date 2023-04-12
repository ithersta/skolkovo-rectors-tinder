package auth.domain.usecases

import NoOpTransaction
import auth.domain.entities.PhoneNumber
import auth.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PhoneNumberIsAllowedUseCaseTest {
    private val samplePhoneNumber = PhoneNumber.of("+79290367450")!!

    @Test
    fun `Duplicate phone number`() {
        val userRepository = mockk<UserRepository>()
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns true
        val isAdminUseCase = mockk<IsAdminUseCase>()
        every { isAdminUseCase.invoke(any()) } returns false
        val phoneNumberIsAllowed = PhoneNumberIsAllowedUseCase(userRepository, NoOpTransaction, isAdminUseCase)
        assertEquals(
            PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber,
            phoneNumberIsAllowed(0L, samplePhoneNumber)
        )
    }

    @Test
    fun ok() {
        val userRepository = mockk<UserRepository>()
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val isAdminUseCase = mockk<IsAdminUseCase>()
        every { isAdminUseCase.invoke(any()) } returns false
        val phoneNumberIsAllowed = PhoneNumberIsAllowedUseCase(userRepository, NoOpTransaction, isAdminUseCase)
        assertEquals(PhoneNumberIsAllowedUseCase.Result.OK, phoneNumberIsAllowed(0L, samplePhoneNumber))
    }
}
