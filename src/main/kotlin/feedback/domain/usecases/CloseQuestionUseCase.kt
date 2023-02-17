package feedback.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.QuestionRepository

@Single
class CloseQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    sealed interface Result {
        object OK : Result
        object Unauthorized : Result
        object NoSuchQuestion : Result
    }

    operator fun invoke(fromUserId: Long, questionId: Long) = transaction {
        val question = questionRepository.get(questionId) ?: return@transaction Result.NoSuchQuestion
        if (question.authorId != fromUserId) return@transaction Result.Unauthorized
        questionRepository.close(questionId)
        Result.OK
    }
}
