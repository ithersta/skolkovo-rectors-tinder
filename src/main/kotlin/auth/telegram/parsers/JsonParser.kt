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
    private val jsonpathRegionPattern: String = "$..region"
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

    private fun createList(pattern: String): List<String> {
        return HashSet(jsonContext.read<Collection<String>>(pattern)).sorted()
    }

    private fun createRegex(pattern: String): Regex {
        return Regex(createList(pattern).joinToString(separator = "|"))
    }

    val cityRegex: Regex = createRegex(jsonpathCityPattern)

    fun getCountries(): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathCountiesPattern).forEach {
                row {
                    dataButton(it, SelectCountryQuery(it))
                }
            }
        }
    }

    fun getDistricts(): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathDistrictsPattern).forEach {
                row {
                    dataButton(it, SelectDistrict(it))
                }
            }
        }
    }

    fun getCitiesFromCIS(country: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathCitiesInCISByCountry(country)).forEach {
                row {
                    dataButton(it, SelectCityInCIS(it))
                }
            }
        }
    }

    fun getRegionsByDistrict(district: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathRegionByDistrict(district)).forEach {
                row {
                    dataButton(it, SelectRegion(it))
                }
            }
        }
    }

    fun getCitiesByRegion(region: String): InlineKeyboardMarkup {
        return inlineKeyboard {
            createList(jsonpathCityByRegion(region)).forEach {
                row {
                    dataButton(it, SelectCity(it))
                }
            }
        }
    }
}
