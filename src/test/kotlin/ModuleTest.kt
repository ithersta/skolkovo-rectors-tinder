import config.BotConfig
import io.mockk.withInstanceFactory
import org.junit.jupiter.api.Test
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules

internal class ModuleTest {
    @Test
    fun `Koin definitions are complete`() {
        koinApplication{
            modules(module)
            checkModules {
                withInstance<BotConfig>(BotConfig(0L, 0L, 0L))
            }
        }

    }
}
