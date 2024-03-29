import backup.BackupRunner
import com.ithersta.tgbotapi.fsm.engines.RegularEngine
import config.readToken
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.bot.settings.limiters.CommonLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import event.telegram.NewEventNotificationSender
import feedback.telegram.FeedbackRequester
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import mute.telegram.UnmuteRunner
import notifications.telegram.DelayedQuestionsNotificationSender
import org.koin.core.context.startKoin
import qna.domain.usecases.AutoCloseOldQuestionsUseCase
import qna.telegram.AcceptedResponsesSender
import qna.telegram.NewQuestionNotificationSender
import qna.telegram.NewResponsesSender

suspend fun main() {
    val application = startKoin { modules(module) }
    val stateMachine: RegularEngine<*, *, *> = application.koin.get()
    val backupRunner: BackupRunner = application.koin.get()
    val feedbackRequester: FeedbackRequester = application.koin.get()
    val unmuteRunner: UnmuteRunner = application.koin.get()
    val newResponsesSender: NewResponsesSender = application.koin.get()
    val delayedQuestionsNotificationSender: DelayedQuestionsNotificationSender = application.koin.get()
    val autoCloseOldQuestions: AutoCloseOldQuestionsUseCase = application.koin.get()
    val acceptedResponsesSender: AcceptedResponsesSender = application.koin.get()
    val newQuestionNotificationSender: NewQuestionNotificationSender = application.koin.get()
    val newEventNotificationSender: NewEventNotificationSender = application.koin.get()
    telegramBot(readToken()) {
        requestsLimiter = CommonLimiter(lockCount = 30, regenTime = 1000)
        client = HttpClient(OkHttp)
    }.buildBehaviourWithLongPolling {
        with(stateMachine) { collectUpdates() }
        with(backupRunner) { setup() }
        with(feedbackRequester) { setup() }
        with(newResponsesSender) { setup() }
        with(delayedQuestionsNotificationSender) { setup() }
        with(unmuteRunner) { setup() }
        acceptedResponsesSender.setup()
        newQuestionNotificationSender.setup()
        newEventNotificationSender.setup()
        autoCloseOldQuestions()
    }.join()
}
