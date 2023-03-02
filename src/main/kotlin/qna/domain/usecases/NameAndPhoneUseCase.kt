package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.ResponsesRepository

@Single
class NameAndPhoneUseCase(
    private val responsesRepository: ResponsesRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): Map<String, String> = transaction {
        return@transaction responsesRepository.getRespondentIdByQuestion(questionId)
    }
}
