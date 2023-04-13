package addorganizations.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import organizations.domain.entities.City
import organizations.domain.repository.CityRepository

@Single
class AddCityUseCase(
    private val cityRepository: CityRepository,
    private val transaction: Transaction
) {
    operator fun invoke(city: String) = transaction {
        cityRepository.add(City.New(city))
    }
}