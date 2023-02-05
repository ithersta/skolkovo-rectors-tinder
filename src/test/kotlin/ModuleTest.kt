import org.junit.jupiter.api.Test
import org.koin.test.check.checkModules

internal class ModuleTest {
    @Test
    fun `Koin definitions are complete`() {
        checkModules {
            modules(module)
        }
    }
}
