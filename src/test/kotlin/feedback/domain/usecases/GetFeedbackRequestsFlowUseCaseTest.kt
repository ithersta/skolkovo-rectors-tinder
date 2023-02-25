package feedback.domain.usecases

import NoOpTransaction
import app.cash.turbine.test
import feedback.domain.repository.FeedbackRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test

class GetFeedbackRequestsFlowUseCaseTest {
    @Test
    fun empty() = runBlocking {
        val feedbackRepository = mockk<FeedbackRepository>(relaxUnitFun = true)
        every { feedbackRepository.getFeedbackRequests(any()) } returns emptyList()
        val getFeedbackRequestsFlow = GetFeedbackRequestsFlowUseCase(
            feedbackRepository = feedbackRepository,
            transaction = NoOpTransaction,
            clock = object : Clock {
                override fun now() = Instant.fromEpochSeconds(0L)
            }
        )
        getFeedbackRequestsFlow().test {
            delay(500L)
            expectNoEvents()
            cancel()
        }
    }
}
