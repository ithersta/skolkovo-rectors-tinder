package qna.domain.usecases

import auth.domain.entities.User
import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.ResponseRepository

@Single
class GetNameAndPhoneUseCase(
    private val responseRepository: ResponseRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): List<User.Details> = transaction {
        return@transaction responseRepository.getRespondentByQuestionId(questionId)
    }
}
