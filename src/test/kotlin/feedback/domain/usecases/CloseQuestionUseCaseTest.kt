package feedback.domain.usecases

import NoOpTransaction
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent
import qna.domain.repository.QuestionRepository
import kotlin.test.assertEquals

internal class CloseQuestionUseCaseTest {
    private val sampleQuestionId = 123L
    private val sampleFromUserId = 1000L
    private val sampleQuestion = Question(
        id = sampleQuestionId,
        authorId = sampleFromUserId,
        intent = QuestionIntent.TestHypothesis,
        subject = "Тема",
        text = "Текст",
        isClosed = false,
        areas = setOf(QuestionArea.Education, QuestionArea.Finance)
    )

    @Test
    fun ok() {
        val questionRepository = mockk<QuestionRepository>(relaxUnitFun = true)
        every { questionRepository.getById(sampleQuestionId) } returns sampleQuestion
        val closeQuestion = CloseQuestionUseCase(questionRepository, NoOpTransaction)
        assertEquals(CloseQuestionUseCase.Result.OK, closeQuestion(sampleFromUserId, sampleQuestionId))
        verify(exactly = 1) { questionRepository.close(sampleQuestionId) }
    }

    @Test
    fun unauthorized() {
        val questionRepository = mockk<QuestionRepository>(relaxUnitFun = true)
        every { questionRepository.getById(sampleQuestionId) } returns sampleQuestion
        val closeQuestion = CloseQuestionUseCase(questionRepository, NoOpTransaction)
        assertEquals(CloseQuestionUseCase.Result.Unauthorized, closeQuestion(1234L, sampleQuestionId))
        verify(exactly = 0) { questionRepository.close(any()) }
    }

    @Test
    fun `No such question`() {
        val questionRepository = mockk<QuestionRepository>(relaxUnitFun = true)
        every { questionRepository.getById(sampleQuestionId) } returns null
        val closeQuestion = CloseQuestionUseCase(questionRepository, NoOpTransaction)
        assertEquals(CloseQuestionUseCase.Result.NoSuchQuestion, closeQuestion(sampleFromUserId, sampleQuestionId))
        verify(exactly = 0) { questionRepository.close(any()) }
    }
}
