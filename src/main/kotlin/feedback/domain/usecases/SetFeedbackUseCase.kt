package feedback.domain.usecases

import common.domain.Transaction
import feedback.domain.repository.FeedbackRepository
import org.koin.core.annotation.Single

@Single
class SetFeedbackUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val transaction: Transaction
) {
    sealed interface Result {
        object OK : Result
        object Unauthorized : Result
    }

    operator fun invoke(fromUserId: Long, responseId: Long, isSuccessful: Boolean) = transaction {
        if (feedbackRepository.getAuthorId(responseId) != fromUserId) return@transaction Result.Unauthorized
        feedbackRepository.setFeedback(responseId, isSuccessful)
        Result.OK
    }
}
