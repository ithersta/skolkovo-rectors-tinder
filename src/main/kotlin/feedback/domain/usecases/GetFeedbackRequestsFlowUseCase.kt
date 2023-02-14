package feedback.domain.usecases

import common.domain.Transaction
import feedback.domain.repository.FeedbackRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Single
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Single
class GetFeedbackRequestsFlowUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val transaction: Transaction,
    private val clock: Clock,
    private val askAfter: Duration = 7.days,
    private val checkInterval: Duration = 1.hours
) {
    operator fun invoke() = flow {
        while (true) {
            val feedbackRequests = transaction {
                feedbackRepository.getFeedbackRequests(clock.now() - askAfter)
            }
            feedbackRequests.forEach { request ->
                emit(request)
                transaction {
                    feedbackRepository.markAsAsked(request.responseId)
                }
            }
            delay(checkInterval)
        }
    }
}
