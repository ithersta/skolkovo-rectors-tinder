package oldQuestion.domain.usecase

import common.domain.Transaction
import oldQuestion.domain.repository.QuestionRepository
import org.koin.core.annotation.Single

@Single
class NameAndPhoneUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionId: Long): Map<String, String> = transaction {
        return@transaction questionRepository.getRespondentIdByQuestion(questionId)
    }
}
