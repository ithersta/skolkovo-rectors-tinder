package auth.domain.usecases

import NoOpTransaction
import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import qna.domain.entities.QuestionArea
import kotlin.test.assertEquals

internal class RegisterUserUseCaseTest {
    private val samplePhoneNumber = PhoneNumber.of("79000000000")!!
    private val sampleUserId = 123L
    private val sampleUserDetails = User.Details(
        id = sampleUserId,
        phoneNumber = samplePhoneNumber,
        course = Course.RectorsSchool,
        name = "Александр",
        city = "Санкт-Петербург",
        job = "Главный специалист",
        organizationType = OrganizationType.School,
        organization = "Организация",
        activityDescription = "Описание деятельности",
        areas = setOf(QuestionArea.Campus, QuestionArea.Finance)
    )

    @Test
    fun `No areas set`() {
        val details = sampleUserDetails.copy(areas = emptySet())
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every { phoneNumberIsAllowedUseCase.invoke(details.phoneNumber) } returns PhoneNumberIsAllowedUseCase.Result.OK
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(sampleUserId) } returns false
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val registerUser = RegisterUserUseCase(phoneNumberIsAllowedUseCase, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.NoAreasSet, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun `Already registered`() {
        val details = sampleUserDetails
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(sampleUserId) } returns true
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every { phoneNumberIsAllowedUseCase.invoke(details.phoneNumber) } returns PhoneNumberIsAllowedUseCase.Result.OK
        val registerUser = RegisterUserUseCase(phoneNumberIsAllowedUseCase, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.AlreadyRegistered, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun `Duplicate phone number`() {
        val details = sampleUserDetails
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(sampleUserId) } returns false
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns true
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every { phoneNumberIsAllowedUseCase.invoke(details.phoneNumber) } returns
            PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber
        val registerUser = RegisterUserUseCase(phoneNumberIsAllowedUseCase, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.DuplicatePhoneNumber, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun `Phone number not allowed`() {
        val details = sampleUserDetails
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(sampleUserId) } returns false
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every { phoneNumberIsAllowedUseCase.invoke(details.phoneNumber) } returns
            PhoneNumberIsAllowedUseCase.Result.PhoneNumberNotAllowed
        val registerUser = RegisterUserUseCase(phoneNumberIsAllowedUseCase, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.PhoneNumberNotAllowed, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun ok() {
        val details = sampleUserDetails
        val userRepository = mockk<UserRepository>()
        every { userRepository.isRegistered(sampleUserId) } returns false
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        every { userRepository.add(details) } returns Unit
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every { phoneNumberIsAllowedUseCase.invoke(details.phoneNumber) } returns
            PhoneNumberIsAllowedUseCase.Result.OK
        val registerUser = RegisterUserUseCase(phoneNumberIsAllowedUseCase, userRepository, NoOpTransaction)
        assertEquals(RegisterUserUseCase.Result.OK, registerUser(details))
        verify(exactly = 1) { userRepository.add(any()) }
    }
}
