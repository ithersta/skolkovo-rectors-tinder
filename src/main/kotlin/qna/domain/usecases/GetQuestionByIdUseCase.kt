package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

@Single
class GetQuestionByIdUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): Question = transaction {
        return@transaction questionRepository.getById(questionId)
    }
}
