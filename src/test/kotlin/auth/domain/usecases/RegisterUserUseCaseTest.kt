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
import organizations.domain.entities.City
import organizations.domain.entities.Organization
import qna.domain.entities.QuestionArea
import kotlin.test.assertEquals

internal class RegisterUserUseCaseTest {
    private val samplePhoneNumber = PhoneNumber.of("79000000000")!!
    private val sampleUserId = 123L
    private val sampleUserDetails = User.NewDetails(
        id = sampleUserId,
        phoneNumber = samplePhoneNumber,
        course = Course.RectorsSchool,
        name = User.Name.ofTruncated("Александр"),
        cityId = 1,
        job = User.Job.ofTruncated("Главный специалист"),
        organizationType = OrganizationType.School,
        organizationId = 1,
        activityDescription = User.ActivityDescription.ofTruncated("Описание деятельности"),
        areas = setOf(QuestionArea.Campus, QuestionArea.Finance)
    )

    @Test
    fun `No areas set`() {
        val details = sampleUserDetails.copy(areas = emptySet())
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every {
            phoneNumberIsAllowedUseCase.invoke(
                sampleUserId,
                details.phoneNumber
            )
        } returns PhoneNumberIsAllowedUseCase.Result.OK
        val userRepository = mockk<UserRepository>()
        every { userRepository.get(sampleUserId) } returns null
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val isAdminUseCase = mockk<IsAdminUseCase>()
        every { isAdminUseCase.invoke(any()) } returns false
        val registerUser = RegisterUserUseCase(
            phoneNumberIsAllowedUseCase,
            userRepository,
            NoOpTransaction,
            isAdminUseCase
        )
        assertEquals(RegisterUserUseCase.Result.NoAreasSet, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun `Already registered`() {
        val details = sampleUserDetails
        val userRepository = mockk<UserRepository>()
        every { userRepository.get(sampleUserId) } returns User.Details(
            sampleUserId,
            samplePhoneNumber,
            Course.RectorsSchool,
            User.Name.ofTruncated("name"),
            User.Job.ofTruncated("job"),
            City(1, City.Name.ofTruncated("city")),
            OrganizationType.ScientificOrganization,
            Organization(1, "organization"),
            User.ActivityDescription.ofTruncated("activity"),
            false,
            setOf(QuestionArea.Campus)
        )
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every {
            phoneNumberIsAllowedUseCase.invoke(
                sampleUserId,
                details.phoneNumber
            )
        } returns PhoneNumberIsAllowedUseCase.Result.OK
        val isAdminUseCase = mockk<IsAdminUseCase>()
        every { isAdminUseCase.invoke(any()) } returns false
        val registerUser = RegisterUserUseCase(
            phoneNumberIsAllowedUseCase,
            userRepository,
            NoOpTransaction,
            isAdminUseCase
        )
        assertEquals(RegisterUserUseCase.Result.AlreadyRegistered, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun `Duplicate phone number`() {
        val details = sampleUserDetails
        val userRepository = mockk<UserRepository>()
        every { userRepository.get(sampleUserId) } returns null
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns true
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every { phoneNumberIsAllowedUseCase.invoke(sampleUserId, details.phoneNumber) } returns
            PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber
        val isAdminUseCase = mockk<IsAdminUseCase>()
        every { isAdminUseCase.invoke(any()) } returns false
        val registerUser = RegisterUserUseCase(
            phoneNumberIsAllowedUseCase,
            userRepository,
            NoOpTransaction,
            isAdminUseCase
        )
        assertEquals(RegisterUserUseCase.Result.DuplicatePhoneNumber, registerUser(details))
        verify(exactly = 0) { userRepository.add(any()) }
    }

    @Test
    fun ok() {
        val newDetails = sampleUserDetails
        val details = User.Details(
            id = newDetails.id,
            phoneNumber = newDetails.phoneNumber,
            course = newDetails.course,
            name = newDetails.name,
            job = newDetails.job,
            city = City(newDetails.cityId, City.Name.ofTruncated("")),
            organizationType = newDetails.organizationType,
            organization = Organization(newDetails.organizationId, ""),
            activityDescription = newDetails.activityDescription,
            isApproved = false,
            areas = newDetails.areas
        )
        val userRepository = mockk<UserRepository>()
        every { userRepository.get(sampleUserId) } returns null
        every { userRepository.containsUserWithPhoneNumber(samplePhoneNumber) } returns false
        every { userRepository.add(newDetails) } returns details
        val phoneNumberIsAllowedUseCase = mockk<PhoneNumberIsAllowedUseCase>()
        every { phoneNumberIsAllowedUseCase.invoke(sampleUserId, newDetails.phoneNumber) } returns
            PhoneNumberIsAllowedUseCase.Result.OK
        val isAdminUseCase = mockk<IsAdminUseCase>()
        every { isAdminUseCase.invoke(any()) } returns false
        val registerUser = RegisterUserUseCase(
            phoneNumberIsAllowedUseCase,
            userRepository,
            NoOpTransaction,
            isAdminUseCase
        )
        assertEquals(RegisterUserUseCase.Result.RequiresApproval(details), registerUser(newDetails))
        verify(exactly = 1) { userRepository.add(any()) }
    }
}
