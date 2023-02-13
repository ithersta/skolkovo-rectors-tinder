package feedback.domain.usecases

import common.domain.Transaction
import feedback.domain.repository.FeedbackRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Single
class GetFeedbackRequestsFlowUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val transaction: Transaction,
    private val clock: Clock
) {
    operator fun invoke() = flow {
        while (true) {
            val feedbackRequests = transaction {
                feedbackRepository.getFeedbackRequests(clock.now() - 7.days)
            }
            feedbackRequests.forEach { request ->
                emit(request)
                transaction {
                    feedbackRepository.markAsAsked(request.responseId)
                }
            }
            delay(1.hours)
        }
    }
}
