import config.BotConfig
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.checkModules

internal class ModuleTest {
    @Test
    fun `Koin definitions are complete`() {
        val testModule = module(createdAtStart = true) {
            single { Database.connect("jdbc:h2:mem:test;MODE=MySQL;", driver = "org.h2.Driver") }
            single { BotConfig(0L, 0L, 0L) }
        }
        koinApplication {
            modules(module, testModule)
            checkModules()
        }
    }
}
