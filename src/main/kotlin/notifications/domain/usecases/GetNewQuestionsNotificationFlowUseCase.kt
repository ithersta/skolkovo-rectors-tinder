package notifications.domain.usecases

import common.domain.Transaction
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.doInfinity
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import notifications.domain.entities.NewQuestionsNotification
import notifications.domain.entities.NotificationPreference
import notifications.domain.repository.NotificationPreferenceRepository
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

private const val DEFAULT_DAILY_HOUR = 19
private val defaultDayOfWeek = DayOfWeek.FRIDAY

@Single
class GetNewQuestionsNotificationFlowUseCase(
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val transaction: Transaction,
    private val timeZone: TimeZone,
    private val clock: Clock,
    private val dailyHour: Int? = null,
    private val dayOfWeek: DayOfWeek? = null
) {
    operator fun invoke() = flow {
        val offset = timeZone.offsetAt(clock.now()).totalSeconds.seconds.inWholeMinutes.toInt()
        buildSchedule(offset) {
            hours { at(dailyHour ?: DEFAULT_DAILY_HOUR) }
            minutes { at(0) }
            seconds { at(0) }
        }.doInfinity {
            val now = adjustedNow()
            emitFor(NotificationPreference.Daily, from = now - 1.days, until = now)
            if (now.toLocalDateTime(timeZone).dayOfWeek == (dayOfWeek ?: defaultDayOfWeek)) {
                emitFor(NotificationPreference.Weekly, from = now - 7.days, until = now)
            }
        }
    }

    private suspend fun FlowCollector<NewQuestionsNotification>.emitFor(
        notificationPreference: NotificationPreference,
        from: Instant,
        until: Instant
    ) = transaction {
        notificationPreferenceRepository.getUserIdsWithPreference(notificationPreference)
    }.forEach { userId ->
        emit(NewQuestionsNotification(userId, from, until, notificationPreference))
    }

    private fun adjustedNow() = clock.now()
        .toLocalDateTime(timeZone).date
        .atTime(dailyHour ?: DEFAULT_DAILY_HOUR, 0)
        .toInstant(timeZone)
}
