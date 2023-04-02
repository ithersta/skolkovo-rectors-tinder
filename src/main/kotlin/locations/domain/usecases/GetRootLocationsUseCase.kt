package locations.domain.usecases

import common.domain.Transaction
import locations.domain.entities.Location
import locations.domain.repository.LocationRepository
import org.koin.core.annotation.Single

@Single
class GetRootLocationsUseCase(
    private val locationRepository: LocationRepository,
    private val transaction: Transaction
) {
    operator fun invoke(): List<Location> = transaction {
        locationRepository.getAllCountries()
    }
}
