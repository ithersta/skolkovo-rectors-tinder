package notifications.domain.usecases

import common.domain.Transaction
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import notifications.domain.entities.NotificationPreference
import notifications.domain.repository.NotificationPreferenceRepository
import org.jobrunr.scheduling.BackgroundJob
import org.jobrunr.scheduling.cron.Cron
import org.koin.core.annotation.Single
import java.time.ZoneId
import kotlin.time.Duration.Companion.days

private const val DEFAULT_DAILY_HOUR = 19
private val defaultDayOfWeek = DayOfWeek.FRIDAY

@Single
class GetNewQuestionsNotificationFlowUseCase(
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val transaction: Transaction,
    private val zoneId: ZoneId,
    private val clock: Clock,
    private val dailyHour: Int? = null,
    private val dayOfWeek: DayOfWeek? = null
) {
    class Notification(val userId: Long, val from: Instant, val until: Instant)

    operator fun invoke() = flow {
        BackgroundJob.scheduleRecurrently(
            "new_questions_daily",
            Cron.daily(dailyHour ?: DEFAULT_DAILY_HOUR),
            zoneId
        ) {
            val userIds = transaction {
                notificationPreferenceRepository.getUserIdsWithPreference(NotificationPreference.Daily)
            }
            val now = adjustedNow()
            val yesterday = now - 1.days
            runBlocking {
                userIds.forEach { userId ->
                    emit(Notification(userId, from = yesterday, until = now))
                }
            }
        }
        BackgroundJob.scheduleRecurrently(
            "new_questions_weekly",
            Cron.weekly(dayOfWeek ?: defaultDayOfWeek, dailyHour ?: DEFAULT_DAILY_HOUR),
            zoneId
        ) {
            val userIds = transaction {
                notificationPreferenceRepository.getUserIdsWithPreference(NotificationPreference.Weekly)
            }
            val now = adjustedNow()
            val previousWeek = now - 7.days
            runBlocking {
                userIds.forEach { userId ->
                    emit(Notification(userId, from = previousWeek, until = now))
                }
            }
        }
    }

    private fun adjustedNow() = clock.now()
        .toLocalDateTime(zoneId.toKotlinTimeZone()).date
        .atTime(dailyHour ?: DEFAULT_DAILY_HOUR, 0)
        .toInstant(zoneId.toKotlinTimeZone())
}
