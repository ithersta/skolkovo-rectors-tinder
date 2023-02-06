package auth.domain.entities

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class PhoneNumberTest {
    @Test
    fun `Valid telegram phone numbers`() {
        assertEquals(PhoneNumber.of("+79000000000")?.value, "79000000000")
        assertEquals(PhoneNumber.of("79000000000")?.value, "79000000000")
        assertEquals(PhoneNumber.of("+7 900 000-00-00")?.value, "79000000000")
    }

    @Test
    fun `Invalid telegram phone numbers`() {
        assertNull(PhoneNumber.of("19000000000"))
        assertNull(PhoneNumber.of("7900000000"))
    }
}
