package services.parsers

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.utils.row
import java.io.File

class JsonParser {
    private val jsonFilePath: String = "cities.json"
    private val jsonData: String = File(jsonFilePath).readText()
    private val jsonContext: DocumentContext = JsonPath.parse(jsonData)

    private val jsonpathCitiesPattern: String = "$..country"
    private val jsonpathDistrictsPattern: String = "$..district"

    private fun createList(pattern: String): List<String> {
        return HashSet(jsonContext.read<Collection<String>>(pattern)).sorted()
    }

    fun getCountries(): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathCitiesPattern).forEach {
                row {
                    dataButton(it, it)
                }
            }
        }
    }

    fun getDistricts(): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathDistrictsPattern).forEach {
                row {
                    dataButton(it, it)
                }
            }
        }
    }

    fun getCitiesFromCIS(country: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList("$.[?(@.country == '$country')].city").forEach {
                row {
                    dataButton(it, it)
                }
            }
        }
    }

    fun getRegionsByDistrict(district: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList("$.[?(@.district ==  '$district')].region").forEach {
                row {
                    dataButton(it, it)
                }
            }
        }
    }

    fun getCitiesByRegion(region: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList("$.[?(@.region ==  '$region')].city").forEach {
                row {
                    dataButton(it, it)
                }
            }
        }
    }
}
