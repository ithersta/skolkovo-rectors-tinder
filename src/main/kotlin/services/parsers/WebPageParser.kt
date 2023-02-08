package services.parsers

import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.utils.row
import org.jsoup.Jsoup
import java.util.*

class WebPageParser {
    private val url = "https://www.skolkovo.ru/navigator/events/"

    private fun takeTitleAndTime(): Vector<String> = runCatching {
        val doc = Jsoup.connect(url).get()
        val titles = doc.getElementsByClass("title announce_title")
        val times = doc.getElementsByClass("announce_date_block align-items-center")
        val meetings = Vector<String>()
        for (i in titles.indices) {
            val title = titles[i] // Get the title element at index i
            val time = times[i] // Get the time element at index i
            val meetingTitle = title.text() // Get the text of the title element
            val meetingTime = time.text() // Get the text of the time element
            meetings.add("$meetingTitle - $meetingTime") // Add title and time to list of meetings
        }
        return@runCatching meetings
    }.onFailure { throw RuntimeException(it) }.getOrThrow()

    fun createButton(): InlineKeyboardMarkup {
        return inlineKeyboard {
            takeTitleAndTime().forEach {
                row {
                    dataButton(it, it)
                }
            }
        }
    }
}
