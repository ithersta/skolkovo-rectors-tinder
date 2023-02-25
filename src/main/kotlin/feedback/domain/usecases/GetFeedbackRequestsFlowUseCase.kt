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

private val defaultAskAfter = 7.days
private val defaultCheckInterval = 1.hours

@Single
class GetFeedbackRequestsFlowUseCase(
    private val feedbackRepository: FeedbackRepository,
    private val transaction: Transaction,
    private val clock: Clock,
    private val askAfter: Duration? = null,
    private val checkInterval: Duration? = null
) {
    operator fun invoke() = flow {
        while (true) {
            val feedbackRequests = transaction {
                feedbackRepository.getFeedbackRequests(clock.now() - (askAfter ?: defaultAskAfter))
            }
            feedbackRequests.forEach { request ->
                emit(request)
                transaction {
                    feedbackRepository.markAsAsked(request.responseId)
                }
            }
            delay(checkInterval ?: defaultCheckInterval)
        }
    }
}
