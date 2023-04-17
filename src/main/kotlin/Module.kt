import auth.domain.usecases.GetUserRoleUseCase
import com.ithersta.tgbotapi.fsm.engines.regularEngine
import common.data.migrations.runMigrations
import common.telegram.strings.CommonStrings
import config.readBotConfig
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import generated.sqliteStateRepository
import io.github.oshai.KotlinLogging
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import notifications.domain.usecases.QuestionNotificationConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import qna.domain.usecases.AutoCloseOldQuestionsUseCase
import qna.domain.usecases.GetNewResponseNotificationFlowUseCase
import java.time.DayOfWeek

private val kLogger = KotlinLogging.logger("Bot Handler")

val dataModule = module(createdAtStart = true) {
    single {
        Database.connect("jdbc:h2:./database;MODE=MySQL;", driver = "org.h2.Driver").also { database ->
            transaction(database) { runMigrations(get()) }
        }
    }
}

val module = module(createdAtStart = true) {
    includes(defaultModule, dataModule)
    single { readBotConfig() }
    single<Clock> { Clock.System }
    single { TimeZone.of("Europe/Moscow") }
    single { QuestionNotificationConfig(notifyAt = LocalTime.parse("15:00"), dayOfWeek = DayOfWeek.TUESDAY) }
    single { GetNewResponseNotificationFlowUseCase.Config() }
    single { AutoCloseOldQuestionsUseCase.Config() }
    single { _ ->
        stateMachine.regularEngine(
            getUser = { get<GetUserRoleUseCase>()(it.chatId) },
            stateRepository = sqliteStateRepository(historyDepth = 1),
            exceptionHandler = { userId, exception ->
                kLogger.error("Exception in chat $userId", exception)
                runCatching {
                    sendTextMessage(userId, CommonStrings.InternalError)
                }
            }
        )
    }
}
