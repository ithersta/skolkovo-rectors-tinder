import backup.BackupRunner
import com.ithersta.tgbotapi.fsm.entities.StateMachine
import config.readToken
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.bot.settings.limiters.CommonLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import org.koin.core.context.startKoin

suspend fun main() {
    val application = startKoin {
        modules(module)
    }
    val stateMachine: StateMachine<*, *, *> = application.koin.get()
    val backupRunner: BackupRunner = application.koin.get()
    telegramBot(readToken()) {
        requestsLimiter = CommonLimiter(lockCount = 30, regenTime = 1000)
        client = HttpClient(OkHttp)
    }.buildBehaviourWithLongPolling {
        with(stateMachine) { collect() }
        with(backupRunner) { setup() }
    }.join()
}
