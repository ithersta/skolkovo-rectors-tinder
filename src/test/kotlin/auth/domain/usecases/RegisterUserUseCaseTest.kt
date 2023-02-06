package auth.domain.usecases

import NoOpTransaction
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.repository.PhoneNumberRepository
import auth.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class RegisterUserUseCaseTest {
    @Test
    fun test() {
        val registeredPhoneNumber = PhoneNumber.of("79000000000")!!
        val allowedPhoneNumber = PhoneNumber.of("79000000001")!!
        val notAllowedPhoneNumber = PhoneNumber.of("79000000002")!!

        val registeredUserDetails = User.Details(0L, registeredPhoneNumber, "", "", "", "", "")
        val allowedNumberUserDetails = User.Details(1L, allowedPhoneNumber, "", "", "", "", "")
        val notAllowedNumberUserDetails = User.Details(2L, notAllowedPhoneNumber, "", "", "", "", "")
        val duplicatePhoneNumberUserDetails = User.Details(2L, registeredPhoneNumber, "", "", "", "", "")

        val userRepository = mockk<UserRepository>()
        every { userRepository.containsUserWithPhoneNumber(registeredPhoneNumber) } returns true
        every { userRepository.containsUserWithPhoneNumber(allowedPhoneNumber) } returns false
        every { userRepository.containsUserWithPhoneNumber(notAllowedPhoneNumber) } returns false
        every { userRepository.get(0L) } returns registeredUserDetails
        every { userRepository.get(1L) } returns null
        every { userRepository.get(2L) } returns null

        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(registeredPhoneNumber) } returns true
        every { phoneNumberRepository.isActive(allowedPhoneNumber) } returns true
        every { phoneNumberRepository.isActive(notAllowedPhoneNumber) } returns false

        val registerUser = RegisterUserUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertTrue { registerUser(registeredUserDetails) == RegisterUserUseCase.Result.AlreadyRegistered }
        assertTrue { registerUser(allowedNumberUserDetails) == RegisterUserUseCase.Result.OK }
        assertTrue { registerUser(notAllowedNumberUserDetails) == RegisterUserUseCase.Result.PhoneNumberNotAllowed }
        assertTrue { registerUser(duplicatePhoneNumberUserDetails) == RegisterUserUseCase.Result.DuplicatePhoneNumber }

        verify(exactly = 0) { userRepository.add(registeredUserDetails) }
        verify(exactly = 1) { userRepository.add(allowedNumberUserDetails) }
        verify(exactly = 0) { userRepository.add(notAllowedNumberUserDetails) }
        verify(exactly = 0) { userRepository.add(duplicatePhoneNumberUserDetails) }
    }
}
