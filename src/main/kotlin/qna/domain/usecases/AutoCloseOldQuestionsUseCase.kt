package qna.domain.usecases

import common.domain.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.annotation.Single
import qna.domain.repository.QuestionRepository
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Single
class AutoCloseOldQuestionsUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction,
    private val clock: Clock,
    private val config: Config
) {
    class Config(val after: Duration = 28.days)

    context(CoroutineScope)
    operator fun invoke() = launch {
        while (true) {
            transaction { questionRepository.closeOlderThan(clock.now() - config.after) }
            delay(1.hours)
        }
    }
}
