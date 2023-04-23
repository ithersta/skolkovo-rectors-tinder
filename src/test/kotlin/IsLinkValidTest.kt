
import event.telegram.validation.IsLinkValid
import org.junit.jupiter.api.Assertions


internal class IsLinkValidTest {
    @org.junit.jupiter.api.Test
    operator fun invoke() {
       // Assertions.assertTrue(IsLinkValid("vk.com"))
        Assertions.assertTrue(IsLinkValid("http://yan-dex.ru"))
       // Assertions.assertTrue(IsLinkValid("www.youtube.com/watch?v=PDxVcTWVIck"))
        Assertions.assertTrue(IsLinkValid("https://lenta.ru/articles/2017/03/06/ruvlogs/"))
        Assertions.assertTrue(IsLinkValid("http://hdstudio.org/serial/kak_ja_vstretil_vashu_mamu_17/19-1-0-52"))
        Assertions.assertFalse(IsLinkValid("sdfsdf"))
        Assertions.assertFalse(IsLinkValid(""))
    }
}
