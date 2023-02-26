package oldQuestion.domain.usecase

import common.domain.Transaction
import oldQuestion.domain.repository.ResponsesRepository
import org.koin.core.annotation.Single

@Single
class NameAndPhoneUseCase(
    private val responsesRepository: ResponsesRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): Map<String, String> = transaction {
        return@transaction responsesRepository.getRespondentIdByQuestion(questionId)
    }
}
