import auth.data.tables.PhoneNumbers
import auth.data.tables.UserAreas
import auth.data.tables.Users
import auth.domain.usecases.GetUserRoleUseCase
import com.ithersta.tgbotapi.fsm.engines.regularEngine
import config.readBotConfig
import generated.sqliteStateRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import notifications.domain.usecases.GetNewQuestionsNotificationFlowUseCase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import qna.data.tables.AcceptedResponses
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses
import qna.domain.usecases.AutoCloseOldQuestionsUseCase
import qna.domain.usecases.GetNewResponseNotificationFlowUseCase

val dataModule = module(createdAtStart = true) {
    single {
        Database.connect("jdbc:h2:./database", driver = "org.h2.Driver").also { database ->
            transaction(database) {
                SchemaUtils.createMissingTablesAndColumns(
                    PhoneNumbers,
                    Users,
                    UserAreas,
                    Questions,
                    QuestionAreas,
                    Responses,
                    AcceptedResponses,
                    MuteSettings,
                    NotificationPreferences
                )
            }
        }
    }
}

val module = module(createdAtStart = true) {
    includes(defaultModule, dataModule)
    single { readBotConfig() }
    single<Clock> { Clock.System }
    single { TimeZone.of("Europe/Moscow") }
    single { GetNewQuestionsNotificationFlowUseCase.Config() }
    single { GetNewResponseNotificationFlowUseCase.Config() }
    single { AutoCloseOldQuestionsUseCase.Config() }
    single { _ ->
        stateMachine.regularEngine(
            getUser = { get<GetUserRoleUseCase>()(it.chatId) },
            stateRepository = sqliteStateRepository(historyDepth = 1),
            exceptionHandler = { _, throwable -> throwable.printStackTrace() }
        )
    }
}
