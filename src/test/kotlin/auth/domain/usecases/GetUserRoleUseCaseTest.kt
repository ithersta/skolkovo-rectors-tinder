package auth.domain.usecases

import NoOpTransaction
import auth.domain.entities.Course
import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import organizations.domain.entities.City
import organizations.domain.entities.Organization
import qna.domain.entities.QuestionArea
import kotlin.test.assertTrue

internal class GetUserRoleUseCaseTest {
    private val sampleUserDetails = User.Details(
        id = 0L,
        phoneNumber = PhoneNumber.of("79000000000")!!,
        course = Course.RectorsSchool,
        name = User.Name.ofTruncated("Александр"),
        city = City(1, City.Name.ofTruncated("city")),
        job = User.Job.ofTruncated("Главный специалист"),
        organizationType = OrganizationType.School,
        organization = Organization(1, "organization"),
        activityDescription = User.ActivityDescription.ofTruncated("Описание деятельности"),
        isApproved = true,
        areas = setOf(QuestionArea.Campus, QuestionArea.Finance)
    )

    @Test
    fun `All roles`() {
        val userRepository = mockk<UserRepository>()
        every { userRepository.get(0L) } returns sampleUserDetails.copy(id = 0L)
        every { userRepository.get(1L) } returns null
        every { userRepository.get(2L) } returns sampleUserDetails.copy(id = 2L)
        every { userRepository.get(3L) } returns sampleUserDetails.copy(id = 3L, isApproved = false)
        val isAdminUseCase = mockk<IsAdminUseCase>()
        every { isAdminUseCase.invoke(any()) } returns false
        every { isAdminUseCase.invoke(0L) } returns true
        val getUser = GetUserRoleUseCase(userRepository, isAdminUseCase, NoOpTransaction)
        assertTrue { getUser(0L) is User.Admin }
        assertTrue { getUser(1L) == User.Unauthenticated }
        assertTrue { getUser(2L) is User.Normal }
        assertTrue { getUser(3L) == User.Unapproved }
    }
}
