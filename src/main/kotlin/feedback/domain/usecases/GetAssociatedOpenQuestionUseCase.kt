package feedback.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository
import qna.domain.repository.ResponseRepository

@Single
class GetAssociatedOpenQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val responseRepository: ResponseRepository,
    private val transaction: Transaction
) {
    operator fun invoke(fromUserId: Long, responseId: Long): Question? = transaction {
        val response = responseRepository.get(responseId) ?: return@transaction null
        val question = questionRepository.getById(response.questionId)
        question?.takeIf { it.isClosed.not() && it.authorId == fromUserId }
    }
}
