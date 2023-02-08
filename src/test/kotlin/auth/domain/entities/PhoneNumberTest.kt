package auth.domain.entities

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class PhoneNumberTest {
    @Test
    fun `Valid telegram phone numbers`() {
        assertEquals("79000000000", PhoneNumber.of("+79000000000")?.value)
        assertEquals("79000000000", PhoneNumber.of("79000000000")?.value)
        assertEquals("79000000000", PhoneNumber.of(" +7 (900) 000-00-00")?.value)
    }

    @Test
    fun `Invalid telegram phone numbers`() {
        assertNull(PhoneNumber.of("19000000000"))
        assertNull(PhoneNumber.of("7900000000"))
        assertNull(PhoneNumber.of("79o00000000"))
        assertNull(PhoneNumber.of("++79000000000"))
        assertNull(PhoneNumber.of("+7ьтпаввапробьтрпаспртоьти9290367450\\ц\\ц "))
    }
}
