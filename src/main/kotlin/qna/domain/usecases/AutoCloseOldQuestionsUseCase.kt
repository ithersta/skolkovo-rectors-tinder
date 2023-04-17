package qna.domain.usecases

import common.domain.Transaction
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import io.github.oshai.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import org.koin.core.annotation.Single
import qna.domain.repository.QuestionRepository
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

private val logger = KotlinLogging.logger { }

@Single
class AutoCloseOldQuestionsUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction,
    private val clock: Clock,
    private val config: Config
) {
    class Config(val after: Duration = 14.days)

    context(CoroutineScope)
    operator fun invoke() = launchSafelyWithoutExceptions {
        while (true) {
            runCatching {
                transaction { questionRepository.closeOlderThan(clock.now() - config.after) }
            }.onFailure { exception ->
                logger.error("Couldn't auto-close old questions", exception)
            }
            delay(1.hours)
        }
    }
}
