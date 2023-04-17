import event.telegram.validation.IsLinkValid
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue

internal class IsLinkValidTest {
    @org.junit.jupiter.api.Test
    operator fun invoke() {
        assertTrue(IsLinkValid("vk.com"))
        assertTrue(IsLinkValid("http://yan-dex.ru"))
        assertTrue(IsLinkValid("www.youtube.com/watch?v=PDxVcTWVIck"))
        assertTrue(IsLinkValid("https://lenta.ru/articles/2017/03/06/ruvlogs/"))
        assertTrue(IsLinkValid("http://hdstudio.org/serial/kak_ja_vstretil_vashu_mamu_17/19-1-0-52"))
        assertFalse(IsLinkValid("sdfsdf"))
        assertFalse(IsLinkValid(""))
    }
}
