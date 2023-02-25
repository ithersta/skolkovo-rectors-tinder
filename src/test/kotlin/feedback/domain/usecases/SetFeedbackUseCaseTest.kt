package feedback.domain.usecases

import NoOpTransaction
import feedback.domain.repository.FeedbackRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SetFeedbackUseCaseTest {
    private val sampleResponseId = 123L
    private val sampleFromUserId = 1000L

    @Test
    fun `Set successful`() {
        val feedbackRepository = mockk<FeedbackRepository>(relaxUnitFun = true)
        every { feedbackRepository.getAuthorId(sampleResponseId) } returns sampleFromUserId
        val setFeedback = SetFeedbackUseCase(feedbackRepository, NoOpTransaction)
        assertEquals(SetFeedbackUseCase.Result.OK, setFeedback(sampleFromUserId, sampleResponseId, true))
        verify(exactly = 1) { feedbackRepository.setFeedback(sampleResponseId, true) }
    }

    @Test
    fun `Set unsuccessful`() {
        val feedbackRepository = mockk<FeedbackRepository>(relaxUnitFun = true)
        every { feedbackRepository.getAuthorId(sampleResponseId) } returns sampleFromUserId
        val setFeedback = SetFeedbackUseCase(feedbackRepository, NoOpTransaction)
        assertEquals(SetFeedbackUseCase.Result.OK, setFeedback(sampleFromUserId, sampleResponseId, false))
        verify(exactly = 1) { feedbackRepository.setFeedback(sampleResponseId, false) }
    }

    @Test
    fun unauthorized() {
        val feedbackRepository = mockk<FeedbackRepository>(relaxUnitFun = true)
        every { feedbackRepository.getAuthorId(sampleResponseId) } returns sampleFromUserId
        val setFeedback = SetFeedbackUseCase(feedbackRepository, NoOpTransaction)
        assertEquals(SetFeedbackUseCase.Result.Unauthorized, setFeedback(1234L, sampleResponseId, true))
        verify(exactly = 0) { feedbackRepository.setFeedback(any(), any()) }
    }
}
