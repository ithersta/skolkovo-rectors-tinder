package notifications.domain.usecases

import common.domain.Transaction
import kotlinx.datetime.Instant
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea
import qna.domain.repository.QuestionRepository

@Single
class GetQuestionsDigestUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(
        from: Instant,
        until: Instant,
        userId: Long,
        area: QuestionArea? = null,
        limit: Int,
        offset: Int
    ) = transaction {
        questionRepository.getQuestionsDigestPaginated(from, until, userId, area, limit, offset)
    }
}
