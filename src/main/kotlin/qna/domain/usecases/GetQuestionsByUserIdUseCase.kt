package qna.domain.usecases

import common.domain.Paginated
import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

@Single
class GetQuestionsByUserIdUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction,
) {
    operator fun invoke(userId: Long, offset: Int, limit: Int): Paginated<Question> = transaction {
        return@transaction questionRepository.getByUserId(userId, offset, limit)
    }
}
