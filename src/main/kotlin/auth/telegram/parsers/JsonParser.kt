package auth.telegram.parsers

import auth.telegram.queries.*
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.utils.row
import generated.dataButton

class JsonParser {
    private val inputStream = this::class.java.getResourceAsStream("/cities.json")
    private val jsonContext: DocumentContext = JsonPath.parse(inputStream)

    private val jsonpathCountiesPattern: String = "$..country"
    private val jsonpathDistrictsPattern: String = "$..district"
    private val jsonpathCityPattern: String = "$..city"

    private fun jsonpathCitiesInCISByCountry(country: String): String {
        return "$.[?(@.country == '$country')].city"
    }

    private fun jsonpathRegionByDistrict(district: String): String {
        return "$.[?(@.district ==  '$district')].region"
    }

    private fun jsonpathCityByRegion(region: String): String {
        return "$.[?(@.region ==  '$region')].city"
    }

    private fun jsonpathUniversities(city: String): String {
        return "$.[?(@.city ==  '$city')].universities[:]"
    }

    private fun createList(pattern: String): List<String> {
        return HashSet(jsonContext.read<Collection<String>>(pattern)).sorted()
    }

    private fun createRegex(pattern: String): Regex {
        return Regex(createList(pattern).joinToString(separator = "|"))
    }

    val cityRegex: Regex = createRegex(jsonpathCityPattern)

    fun getInlineKeyboardMarkupCountries(): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathCountiesPattern).forEach {
                row {
                    dataButton(it, SelectCountryQuery(it))
                }
            }
        }
    }

    fun getInlineKeyboardMarkupDistricts(): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathDistrictsPattern).forEach {
                row {
                    dataButton(it, SelectDistrictQuery(it))
                }
            }
        }
    }

    fun getInlineKeyboardMarkupCitiesFromCIS(country: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathCitiesInCISByCountry(country)).forEach {
                row {
                    dataButton(it, SelectCityInCISQuery(it))
                }
            }
        }
    }

    fun getInlineKeyboardMarkupRegionsByDistrict(district: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathRegionByDistrict(district)).forEach {
                row {
                    dataButton(it, SelectRegionQuery(it))
                }
            }
        }
    }

    fun getInlineKeyboardMarkupCitiesByRegion(region: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathCityByRegion(region)).forEach {
                row {
                    dataButton(it, SelectCityQuery(it))
                }
            }
        }
    }

    fun getInlineKeyboardMarkupUniversities(city: String): InlineKeyboardMarkup {
        val list: List<String> = jsonContext.read<List<String>>("$.[?(@.city ==  '$city')].universities[:]")
        list.forEach {
            println(it)
        }
        return inlineKeyboard {
            list.forEach {
                row {
                    dataButton(it, SelectUniversityQuery(it))
                }
            }
        }
    }
}
