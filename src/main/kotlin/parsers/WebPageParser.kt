package parsers

import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class WebPageParser {
    private val url = "https://www.skolkovo.ru/navigator/events/"
    fun takeTitleAndTime() {
        try {
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
            println(meetings) // Print out all meetings in list
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}