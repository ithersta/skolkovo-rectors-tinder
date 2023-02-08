package services.parsers

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.utils.row
import java.io.File

class JsonParser {
    private val jsonFilePath: String = "cities.json"
    private val jsonData: String = File(jsonFilePath).readText()
    private val jsonContext: DocumentContext = JsonPath.parse(jsonData)

    private val jsonpathCitiesPattern: String = "$..country"
    private val jsonpathDistrictsPattern: String = "$..district"


    private fun createList(pattern: String): HashSet<String> {
        val countriesButton = mutableListOf<CallbackDataInlineKeyboardButton>()
        return HashSet(jsonContext.read<Collection<String>>(pattern))
    }

    fun getCountries(): ReplyKeyboardMarkup {
        val nameReplyMarkup = replyKeyboard(
            resizeKeyboard = true, oneTimeKeyboard = true
        ) {
            createList(jsonpathCitiesPattern).forEach {
                row {
                    simpleButton(it)
                }
            }
        }
        return nameReplyMarkup
    }

    fun getDistricts(): ReplyKeyboardMarkup {
        val nameReplyMarkup = replyKeyboard(
            resizeKeyboard = true, oneTimeKeyboard = true
        ) {
            createList(jsonpathDistrictsPattern).forEach {
                row {
                    simpleButton(it)
                }
            }

        }
        return nameReplyMarkup
    }

    fun getCitiesFromCIS(country: String): ReplyKeyboardMarkup {
        val nameReplyMarkup = replyKeyboard(
            resizeKeyboard = true, oneTimeKeyboard = true
        ) {
            createList("$.[?(@.country == '$country')].city").forEach {
                row {
                    simpleButton(it)
                }
            }
        }
        return nameReplyMarkup
    }

    fun getRegionsByDistrict(district: String): ReplyKeyboardMarkup {
        val nameReplyMarkup = replyKeyboard(
            resizeKeyboard = true, oneTimeKeyboard = true
        ) {
            createList("$.[?(@.district ==  '$district')].region").forEach {
                row {
                    simpleButton(it)
                }
            }
        }
        return nameReplyMarkup
    }

    fun getCitiesByRegion(region: String): ReplyKeyboardMarkup {
        val nameReplyMarkup = replyKeyboard(
            resizeKeyboard = true, oneTimeKeyboard = true
        ) {
            createList("$.[?(@.region ==  '$region')].city").forEach {
                row {
                    simpleButton(it)
                }
            }
        }
        return nameReplyMarkup
    }
}
