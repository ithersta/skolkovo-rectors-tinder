package notifications.domain.usecases

import common.domain.Transaction
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.doInfinity
import dev.inmo.tgbotapi.extensions.utils.flatMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.datetime.*
import notifications.domain.entities.NewQuestionsNotification
import notifications.domain.entities.NotificationPreference
import notifications.domain.repository.NotificationPreferenceRepository
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.seconds

class QuestionNotificationConfig(
    val notifyAt: LocalTime,
    val dayOfWeek: DayOfWeek
)

@Single
class GetNewQuestionsNotificationFlowUseCase(
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val transaction: Transaction,
    private val timeZone: TimeZone,
    private val clock: Clock,
    private val config: QuestionNotificationConfig
) {
    private val testNotificationChannel = Channel<NotificationPreference>(BUFFERED)

    operator fun invoke() = merge(dailyFlow(), testFlow())

    suspend fun triggerTestNotification(notificationPreference: NotificationPreference) {
        testNotificationChannel.send(notificationPreference)
    }

    private fun testFlow() = testNotificationChannel.receiveAsFlow().flatMap { notificationPreference ->
        generateNotifications(notificationPreference, now = clock.now())
    }

    private fun dailyFlow() = flow {
        val offset = timeZone.offsetAt(clock.now()).totalSeconds.seconds.inWholeMinutes.toInt()
        buildSchedule(offset) {
            hours { at(config.notifyAt.hour) }
            minutes { at(config.notifyAt.minute) }
            seconds { at(config.notifyAt.second) }
        }.doInfinity { _ ->
            val now = adjustedNow()
            generateNotifications(NotificationPreference.Daily, now = now).forEach { emit(it) }
            if (now.toLocalDateTime(timeZone).dayOfWeek == (config.dayOfWeek)) {
                generateNotifications(NotificationPreference.Weekly, now = now).forEach { emit(it) }
            }
        }
    }

    private fun generateNotifications(
        notificationPreference: NotificationPreference,
        now: Instant
    ) = run {
        val from = now - notificationPreference.duration
        transaction {
            notificationPreferenceRepository.getUserIdsWithPreference(notificationPreference)
        }.map { userId ->
            NewQuestionsNotification(userId, from = from, until = now, notificationPreference)
        }
    }

    private fun adjustedNow() = clock.now()
        .toLocalDateTime(timeZone).date
        .atTime(config.notifyAt)
        .toInstant(timeZone)
}
