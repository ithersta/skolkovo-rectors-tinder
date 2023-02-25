package common.telegram

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val defaultDelay = 0.2.seconds

@Single
class MassSendLimiter(
    private val delay: Duration? = null
) {
    private val mutex = Mutex()
    suspend fun wait() = mutex.withLock {
        delay(delay ?: defaultDelay)
    }
}
