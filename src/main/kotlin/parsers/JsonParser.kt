package parsers

import com.jayway.jsonpath.JsonPath
import java.util.*

class JsonParser {
    private var index = -1
    private val fields = ArrayList(listOf("district", "region", "city"))
    var scanner = Scanner(System.`in`)

    private fun capitalize(str: String): String {
        return str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1).lowercase(Locale.getDefault())
    }

    private fun searchByField(fieldName: String, fieldParam: String): String {
        return "$.[?(@.$fieldName == '$fieldParam')]"
    }

    private fun retrieveValuesByKey(json: String?, key: String): String {
        val path = "$..$key"
        return HashSet(JsonPath.parse(json).read<Collection<*>>(path)).toString()
    }

    fun searchResultLooped(json: String?, fieldName: String, fieldParam: String): String {
        var json = json
        var fieldName = fieldName
        var fieldParam = fieldParam
        var searchResult = ""
        var isContinueSearching = true
        while (isContinueSearching) { // loop instead of recursion
            index++ // increment index
            val param = capitalize(fieldParam) // name of country, county, region, city
            val pattern = searchByField(fieldName, param) // Creates a patch to look for
            val cities = JsonPath.parse(json).read<List<String>>(pattern) // list of cities similar in pattern
            println(retrieveValuesByKey(json, fieldName)) // list of countries, counties, regions, cities
            val limitedJsonFile = cities.toString() // json
            if (limitedJsonFile.matches(Regex(pattern = "^.*?,.*?,.*?,.*?,.*?$"))) { // check if there are more than 4 elements in the list
                json = limitedJsonFile // update json string for next iteration of the loop
                fieldName = fields[index] // update field name for next iteration of the loop
                fieldParam = scanner.nextLine() // update parameter for next iteration of the loop
            } else { // if there are less than 4 elements in the list - stop searching and exit from the loop
                isContinueSearching = false // set flag too false to exit from the loop
                searchResult = limitedJsonFile
            }
        }
        if (index == 0) {
            val cities = JsonPath.parse(searchResult).read<List<String>>("$..city") // list of cities
            if (cities.size > 1) {
                println(cities.toString()) // print  cities
                searchResult = searchByField("city", scanner.nextLine())
            }
        }
        return searchResult
    }
}
