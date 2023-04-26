package event.domain.entities

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class EventUrlTest {
    @Test
    operator fun invoke() {
        assertTrue(Event.Url.of("http://yan-dex.ru").isRight())
        assertTrue(Event.Url.of("https://lenta.ru/articles/2017/03/06/ruvlogs/").isRight())
        assertTrue(Event.Url.of("http://hdstudio.org/serial/kak_ja_vstretil_vashu_mamu_17/19-1-0-52").isRight())
        assertTrue(Event.Url.of("sdfsdf").isLeft { it == Event.Url.Error.InvalidUrl })
        assertTrue(Event.Url.of("").isLeft { it == Event.Url.Error.InvalidUrl })
    }
}
