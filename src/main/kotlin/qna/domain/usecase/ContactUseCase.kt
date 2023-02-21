package qna.domain.usecase

import auth.domain.entities.User
import auth.domain.repository.QuestionRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class ContactUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): User.Details = transaction {
        return@transaction questionRepository.getUserInfoByQuestionId(questionId)
    }
}
