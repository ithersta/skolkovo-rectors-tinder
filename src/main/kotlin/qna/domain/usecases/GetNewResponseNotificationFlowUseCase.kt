package qna.domain.usecases

import common.domain.Transaction
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.doInfinity
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository
import kotlin.time.Duration.Companion.seconds

@Single
class GetNewResponseNotificationFlowUseCase(
    private val addResponseUseCase: AddResponseUseCase,
    private val getQuestionByIdUseCase: GetQuestionByIdUseCase,
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction,
    private val timeZone: TimeZone,
    private val clock: Clock,
    private val config: Config
) {
    class Config(val firstResponsesThreshold: Int = 3, val dailyHour: Int = 15)

    operator fun invoke() = merge(createOnThresholdFlow(), createDailyFlow())

    private fun createOnThresholdFlow() = addResponseUseCase.newResponses
        .filter { it.responseCount == config.firstResponsesThreshold }
        .mapNotNull {
            NewResponseNotification.OnThreshold(
                count = it.responseCount,
                question = getQuestionByIdUseCase(it.response.questionId) ?: return@mapNotNull null
            )
        }

    private fun createDailyFlow() = flow {
        val offset = timeZone.offsetAt(clock.now()).totalSeconds.seconds.inWholeMinutes.toInt()
        buildSchedule(offset) {
            hours { at(config.dailyHour) }
            minutes { at(0) }
            seconds { at(0) }
        }.doInfinity { _ ->
            val questions = transaction { questionRepository.getWithUnsentResponses() }
            questions.forEach { emit(NewResponseNotification.Daily(it)) }
        }
    }
}

sealed interface NewResponseNotification {
    val question: Question

    data class OnThreshold(val count: Int, override val question: Question) : NewResponseNotification
    data class Daily(override val question: Question) : NewResponseNotification
}
