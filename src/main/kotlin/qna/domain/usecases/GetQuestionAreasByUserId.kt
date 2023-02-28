package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea
import qna.domain.repository.QuestionRepository

@Single
class GetQuestionAreasByUserId(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long): List<QuestionArea> = transaction {
        return@transaction questionRepository.getQuestionAreasByUserId(userId).toSet().toList()
    }
}
