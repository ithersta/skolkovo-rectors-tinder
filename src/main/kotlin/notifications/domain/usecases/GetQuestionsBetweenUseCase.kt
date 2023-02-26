package notifications.domain.usecases

import common.domain.Transaction
import kotlinx.datetime.Instant
import org.koin.core.annotation.Single
import qna.domain.repository.QuestionRepository

@Single
class GetQuestionsBetweenUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(from: Instant, until: Instant, excludeUserId: Long, limit: Int, offset: Int) = transaction {
        questionRepository.getBetweenPaginated(from, until, excludeUserId, limit, offset)
    }
}
