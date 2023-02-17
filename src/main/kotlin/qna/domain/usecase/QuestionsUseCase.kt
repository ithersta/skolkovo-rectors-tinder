package qna.domain.usecase

import auth.domain.repository.QuestionsRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class QuestionsUseCase(
    private val questionsRepository: QuestionsRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userSubject: String): List<String> = transaction {
        return@transaction questionsRepository.getListOfCurrentIssues(userSubject)
    }
}
