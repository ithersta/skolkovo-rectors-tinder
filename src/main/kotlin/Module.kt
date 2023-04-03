import auth.domain.usecases.GetUserRoleUseCase
import com.ithersta.tgbotapi.fsm.engines.regularEngine
import common.data.migrations.runMigrations
import common.telegram.strings.CommonStrings
import config.readBotConfig
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import generated.sqliteStateRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import notifications.domain.usecases.QuestionNotificationConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
<<<<<<< HEAD
import organizations.data.tables.Cities
import organizations.data.tables.Organizations
import qna.data.tables.AcceptedResponses
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses
=======
>>>>>>> main
import qna.domain.usecases.AutoCloseOldQuestionsUseCase
import qna.domain.usecases.GetNewResponseNotificationFlowUseCase
import java.time.DayOfWeek

val dataModule = module(createdAtStart = true) {
    single {
        Database.connect("jdbc:h2:./database;MODE=MySQL;", driver = "org.h2.Driver").also { database ->
            transaction(database) { runMigrations() }
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
            exceptionHandler = { userId, throwable ->
                throwable.printStackTrace()
                runCatching {
                    sendTextMessage(userId, CommonStrings.InternalError)
                }
            }
        )
    }
}
