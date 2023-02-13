package auth.domain.usecases

import NoOpTransaction
import auth.domain.entities.PhoneNumber
import auth.domain.repository.PhoneNumberRepository
import auth.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PhoneNumberIsAllowedUseCaseTest{
    private val samplePhoneNumber = PhoneNumber.of("+79290367450")!!
    @Test
    fun `Duplicate phone number`() {
        val userRepository = mockk<UserRepository>()
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns true
        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(samplePhoneNumber) } returns true
        val phoneNumberIsAllowed = PhoneNumberIsAllowedUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertEquals(PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber, phoneNumberIsAllowed(samplePhoneNumber))
    }

    @Test
    fun `Phone number not allowed`() {
        val userRepository = mockk<UserRepository>()
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(samplePhoneNumber) } returns false
        val phoneNumberIsAllowed = PhoneNumberIsAllowedUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertEquals(PhoneNumberIsAllowedUseCase.Result.PhoneNumberNotAllowed, phoneNumberIsAllowed(samplePhoneNumber))
    }

    @Test
    fun ok() {
        val userRepository = mockk<UserRepository>()
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(samplePhoneNumber) } returns true
        val phoneNumberIsAllowed = PhoneNumberIsAllowedUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertEquals(PhoneNumberIsAllowedUseCase.Result.OK, phoneNumberIsAllowed(samplePhoneNumber))
    }


}