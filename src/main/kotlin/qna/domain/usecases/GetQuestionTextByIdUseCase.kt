package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.QuestionRepository

@Single
class GetQuestionTextByIdUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): String = transaction {
        return@transaction questionRepository.getTextById(questionId)
    }
}
