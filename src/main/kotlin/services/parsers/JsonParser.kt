package services.parsers

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import java.io.File


class JsonParser {
    private val jsonFilePath: String = "cities.json"
    private val jsonData: String = File(jsonFilePath).readText()
    private val jsonContext: DocumentContext = JsonPath.parse(jsonData)

    private val jsonpathCitiesPattern: String = "$..country"
    private val jsonpathDistrictsPattern: String = "$..district"

    private fun createList(pattern: String): List<CallbackDataInlineKeyboardButton> {
        val countriesButton = mutableListOf<CallbackDataInlineKeyboardButton>()
        val hashSet: HashSet<String> = HashSet(jsonContext.read<Collection<String>>(pattern))
        for (item in hashSet) {
            countriesButton.add(CallbackDataInlineKeyboardButton(text = item, callbackData = item))
        }
        return countriesButton.toList()
    }

    fun getCountries(): List<CallbackDataInlineKeyboardButton> {
        return createList(jsonpathCitiesPattern)
    }

    fun getDistricts(): List<CallbackDataInlineKeyboardButton> {
        return createList(jsonpathDistrictsPattern)
    }

    fun getCitiesFromCIS(country: String): List<CallbackDataInlineKeyboardButton> {
        return createList("$.[?(@.country == '$country')].city")
    }

    fun getRegionsByDistrict(district: String): List<CallbackDataInlineKeyboardButton> {
        return createList("$.[?(@.district ==  '$district')].region")
    }

    fun getCitiesByRegion(region: String): List<CallbackDataInlineKeyboardButton> {
        return createList("$.[?(@.region ==  '$region')].city")
    }
}
