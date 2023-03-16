import config.BotConfig
import org.junit.jupiter.api.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.checkModules

internal class ModuleTest {
    @Test
    fun `Koin definitions are complete`() {
        val testModule = module(createdAtStart = true) {
            single { BotConfig(0L, 0L, 0L) }
        }
        koinApplication {
            modules(module, testModule)
            checkModules()
        }
    }
}
