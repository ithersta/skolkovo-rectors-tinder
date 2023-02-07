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
import qna.domain.entities.QuestionArea
import kotlin.test.assertEquals

internal class RegisterUserUseCaseTest {
    private val samplePhoneNumber = PhoneNumber.of("79000000000")!!

    @Test
    fun `No areas set`() {
        val details = User.Details(0L, samplePhoneNumber, "", "", "", "", "", emptySet())
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(0L) } returns false
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(samplePhoneNumber) } returns true
        val registerUser = RegisterUserUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.NoAreasSet, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun `Already registered`() {
        val details = User.Details(0L, samplePhoneNumber, "", "", "", "", "", setOf(QuestionArea.Campus))
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(0L) } returns true
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(samplePhoneNumber) } returns true
        val registerUser = RegisterUserUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.AlreadyRegistered, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun `Duplicate phone number`() {
        val details = User.Details(0L, samplePhoneNumber, "", "", "", "", "", setOf(QuestionArea.Campus))
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(0L) } returns false
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns true
        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(samplePhoneNumber) } returns true
        val registerUser = RegisterUserUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.DuplicatePhoneNumber, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun `Phone number not allowed`() {
        val details = User.Details(0L, samplePhoneNumber, "", "", "", "", "", setOf(QuestionArea.Campus))
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(0L) } returns false
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(samplePhoneNumber) } returns false
        val registerUser = RegisterUserUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.PhoneNumberNotAllowed, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun ok() {
        val details = User.Details(0L, samplePhoneNumber, "", "", "", "", "", setOf(QuestionArea.Campus))
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(0L) } returns false
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberRepository = mockk<PhoneNumberRepository>()
        every { phoneNumberRepository.isActive(samplePhoneNumber) } returns true
        val registerUser = RegisterUserUseCase(phoneNumberRepository, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.OK, registerUser(details))
        verify(exactly = 1) { userRepository.add(any()) }
    }
}
