package qna.domain.usecases

import common.domain.Transaction
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.doInfinity
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import org.koin.core.annotation.Single
import qna.domain.entities.NewResponseNotification
import qna.domain.repository.QuestionRepository
import qna.domain.repository.ResponseRepository
import kotlin.time.Duration.Companion.seconds

@Single
class GetNewResponseNotificationFlowUseCase(
    private val addResponseUseCase: AddResponseUseCase,
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction,
    private val timeZone: TimeZone,
    private val clock: Clock,
    private val config: Config? = null
) {
    class Config(val firstResponsesCount: Int = 3, val dailyHour: Int = 19)

    operator fun invoke(): Flow<NewResponseNotification> {
        val config = config ?: Config()
        val offset = timeZone.offsetAt(clock.now()).totalSeconds.seconds.inWholeMinutes.toInt()
        val firstResponsesFlow = addResponseUseCase.newResponses
            .filter { it.responseCount == config.firstResponsesCount }
            .map {
                transaction {
                    val question = questionRepository.getById(it.response.questionId)!!
                    NewResponseNotification(
                        questionAuthorId = question.authorId
                    )
                }
            }
            .catch { }
        val periodicResponsesFlow = flow<NewResponseNotification> {
            buildSchedule(offset) {
                hours { at(config.dailyHour) }
                minutes { at(0) }
                seconds { at(0) }
            }.doInfinity {
                TODO()
            }
        }
        return merge(firstResponsesFlow, periodicResponsesFlow)
    }
}
