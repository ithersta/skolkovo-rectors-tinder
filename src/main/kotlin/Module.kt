import auth.data.tables.PhoneNumbers
import auth.data.tables.Users
import config.readBotConfig
import feedback.data.tables.Feedback
import mute.data.entities.MuteSettings
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import qna.data.tables.Questions
import qna.data.tables.Responses

val dataModule = module(createdAtStart = true) {
    single {
        Database.connect("jdbc:h2:./database", driver = "org.h2.Driver").also { database ->
            transaction(database) {
                SchemaUtils.createMissingTablesAndColumns(
                    PhoneNumbers,
                    Users,
                    Questions,
                    Responses,
                    Feedback,
                    MuteSettings
                )
            }
        }
    }
}

val module = module(createdAtStart = true) {
    includes(defaultModule, dataModule)
    single { readBotConfig() }
    single { stateMachine(get()) }
}
