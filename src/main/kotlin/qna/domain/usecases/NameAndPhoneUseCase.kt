package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.ResponseRepository

@Single
class NameAndPhoneUseCase(
    private val responseRepository: ResponseRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): Map<String, String> = transaction {
        return@transaction responseRepository.getRespondentIdByQuestion(questionId)
    }
}
