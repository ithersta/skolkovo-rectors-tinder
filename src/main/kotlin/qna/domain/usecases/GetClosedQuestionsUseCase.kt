package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

@Single
class GetClosedQuestionsUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(authorId: Long): List<Question> = transaction {
        return@transaction questionRepository.getClosed(authorId)
    }
}
