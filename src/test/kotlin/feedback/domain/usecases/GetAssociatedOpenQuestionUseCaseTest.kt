package feedback.domain.usecases

import NoOpTransaction
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Test
import qna.domain.entities.*
import qna.domain.repository.QuestionRepository
import qna.domain.repository.ResponseRepository
import kotlin.test.assertEquals

internal class GetAssociatedOpenQuestionUseCaseTest {
    private val sampleQuestionId = 123L
    private val sampleFromUserId = 1000L
    private val sampleResponseId = 124L
    private val sampleQuestion = Question(
        id = sampleQuestionId,
        authorId = sampleFromUserId,
        intent = QuestionIntent.TestHypothesis,
        subject = Question.Subject.ofTruncated("Тема"),
        text = Question.Text.ofTruncated("Текст"),
        isClosed = false,
        areas = setOf(QuestionArea.Education, QuestionArea.Finance),
        at = Clock.System.now(),
        hideFrom = HideFrom.NoOne
    )
    private val sampleResponse = Response(
        id = sampleResponseId,
        questionId = sampleQuestionId,
        respondentId = 12345L
    )

    @Test
    fun ok() {
        val questionRepository = mockk<QuestionRepository>()
        every { questionRepository.getById(sampleQuestionId) } returns sampleQuestion
        val responseRepository = mockk<ResponseRepository>()
        every { responseRepository.get(sampleResponseId) } returns sampleResponse
        val getAssociatedOpenQuestion =
            GetAssociatedOpenQuestionUseCase(questionRepository, responseRepository, NoOpTransaction)
        assertEquals(sampleQuestion, getAssociatedOpenQuestion(sampleFromUserId, sampleResponseId))
    }

    @Test
    fun unauthorized() {
        val questionRepository = mockk<QuestionRepository>()
        every { questionRepository.getById(sampleQuestionId) } returns sampleQuestion
        val responseRepository = mockk<ResponseRepository>()
        every { responseRepository.get(sampleResponseId) } returns sampleResponse
        val getAssociatedOpenQuestion =
            GetAssociatedOpenQuestionUseCase(questionRepository, responseRepository, NoOpTransaction)
        assertEquals(null, getAssociatedOpenQuestion(12345L, sampleResponseId))
    }

    @Test
    fun `Question closed`() {
        val questionRepository = mockk<QuestionRepository>()
        every { questionRepository.getById(sampleQuestionId) } returns sampleQuestion.copy(isClosed = true)
        val responseRepository = mockk<ResponseRepository>()
        every { responseRepository.get(sampleResponseId) } returns sampleResponse
        val getAssociatedOpenQuestion =
            GetAssociatedOpenQuestionUseCase(questionRepository, responseRepository, NoOpTransaction)
        assertEquals(null, getAssociatedOpenQuestion(sampleFromUserId, sampleResponseId))
    }
}
