package qna.domain.usecases

import NoOpTransaction
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent
import qna.domain.entities.Response
import qna.domain.repository.ResponseRepository

@OptIn(ExperimentalCoroutinesApi::class)
internal class AddResponseUseCaseTest {
    private val sampleQuestionId = 1234L
    private val sampleRespondentId = 1L
    private val sampleResponseId = 2L
    private val sampleQuestion = Question(
        authorId = 12L,
        intent = QuestionIntent.FreeForm,
        subject = "Тема",
        text = "Текст",
        isClosed = false,
        areas = setOf(QuestionArea.Finance, QuestionArea.Education),
        at = Clock.System.now(),
        id = sampleQuestionId
    )
    private val sampleResponse = Response(
        id = sampleResponseId,
        questionId = sampleQuestionId,
        respondentId = sampleRespondentId
    )

    @Test
    fun ok() = runTest {
        val responseRepository = mockk<ResponseRepository>()
        val getQuestionByIdUseCase = mockk<GetQuestionByIdUseCase>()
        every { getQuestionByIdUseCase(sampleQuestionId) } returns sampleQuestion
        every { responseRepository.add(sampleQuestionId, sampleRespondentId) } returns sampleResponseId
        every { responseRepository.count(sampleQuestionId) } returns 3
        val addResponse = AddResponseUseCase(responseRepository, getQuestionByIdUseCase, NoOpTransaction)
        val result = addResponse(sampleQuestionId, sampleRespondentId)
        assertEquals(AddResponseUseCase.Result.OK(sampleResponse), result)
        addResponse.newResponses.test {
            assertEquals(AddResponseUseCase.NewResponseMessage(sampleResponse, 3), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `No such question`() = runTest {
        val responseRepository = mockk<ResponseRepository>()
        val getQuestionByIdUseCase = mockk<GetQuestionByIdUseCase>()
        every { getQuestionByIdUseCase(sampleQuestionId) } returns null
        every { responseRepository.add(sampleQuestionId, sampleRespondentId) } returns sampleResponseId
        val addResponse = AddResponseUseCase(responseRepository, getQuestionByIdUseCase, NoOpTransaction)
        val result = addResponse(sampleQuestionId, sampleRespondentId)
        assertEquals(AddResponseUseCase.Result.NoSuchQuestion, result)
    }

    @Test
    fun `Question closed`() = runTest {
        val responseRepository = mockk<ResponseRepository>()
        val getQuestionByIdUseCase = mockk<GetQuestionByIdUseCase>()
        every { getQuestionByIdUseCase(sampleQuestionId) } returns sampleQuestion.copy(isClosed = true)
        every { responseRepository.add(sampleQuestionId, sampleRespondentId) } returns sampleResponseId
        val addResponse = AddResponseUseCase(responseRepository, getQuestionByIdUseCase, NoOpTransaction)
        val result = addResponse(sampleQuestionId, sampleRespondentId)
        assertEquals(AddResponseUseCase.Result.QuestionClosed, result)
    }
}
