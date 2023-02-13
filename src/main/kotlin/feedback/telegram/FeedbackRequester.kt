package feedback.telegram

import feedback.domain.usecases.GetFeedbackRequestsFlowUseCase
import org.koin.core.annotation.Single

@Single
class FeedbackRequester(
    private val getFeedbackRequestsFlow: GetFeedbackRequestsFlowUseCase
) {

}
