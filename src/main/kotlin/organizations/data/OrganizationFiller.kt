package organizations.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jetbrains.exposed.sql.Transaction
import org.koin.core.annotation.Single
import organizations.domain.entities.City
import organizations.domain.entities.Organization
import organizations.domain.repository.CityRepository
import organizations.domain.repository.OrganizationRepository
import java.io.InputStream

@Single
class OrganizationFiller(
    private val cityRepository: CityRepository,
    private val organizationRepository: OrganizationRepository
) {
    @Serializable
    private class JsonCity(
        val city: String,
        val universities: List<String>
    )

    @OptIn(ExperimentalSerializationApi::class)
    fun Transaction.loadFromJson(inputStream: InputStream) {
        if (cityRepository.getAll().isNotEmpty()) return
        val cities = Json.decodeFromStream<List<JsonCity>>(inputStream)
        cities.forEach { jsonCity ->
            val city = cityRepository.add(City.New(name = jsonCity.city))
            jsonCity.universities.forEach { jsonUniversity ->
                organizationRepository.add(Organization.New(name = jsonUniversity, cityId = city.id))
            }
        }
    }
}
