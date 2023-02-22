package qna.domain.usecase

import auth.domain.repository.QuestionRepository
import common.domain.Transaction
import org.koin.core.annotation.Single


@Single
class UserIdUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): Long = transaction {
        return@transaction questionRepository.getUserIdByQuestionId(questionId)
    }
}
