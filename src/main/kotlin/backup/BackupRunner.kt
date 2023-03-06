package backup

import config.BotConfig
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import java.io.File
import java.time.Instant
import kotlin.time.Duration.Companion.hours

@Single
class BackupRunner(
    private val botConfig: BotConfig,
    private val database: Database
) {
    private val mutex = Mutex()

    fun BehaviourContext.setup() = launchSafelyWithoutExceptions {
        val chatId = botConfig.backupUserId.toChatId()
        while (true) {
            backup(chatId)
            delay(8.hours)
        }
    }

    private suspend fun BehaviourContext.backup(chatId: ChatId) = runCatching {
        val filename = "${Instant.now()}.zip".replace(':', '-')
        val document = mutex.withLock {
            transaction(database) {
                exec("BACKUP TO '$filename'")
            }
            val file = File(filename)
            file.readBytes().asMultipartFile(filename).also {
                file.delete()
            }
        }
        sendDocument(chatId, document, disableNotification = true)
    }.onFailure { it.printStackTrace() }
}
