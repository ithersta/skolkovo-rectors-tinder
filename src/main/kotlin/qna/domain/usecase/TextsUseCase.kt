package qna.domain.usecase

import auth.domain.repository.QuestionRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class TextsUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): String = transaction {
        return@transaction questionRepository.getQuestionTextByQuestionId(questionId)
    }
}