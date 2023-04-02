package locations.domain.usecases

import common.domain.Transaction
import locations.domain.entities.Location
import locations.domain.repository.LocationRepository
import org.koin.core.annotation.Single

@Single
class GetLocationChildrenUseCase(
    private val locationRepository: LocationRepository,
    private val transaction: Transaction
) {
    operator fun invoke(location: Location.WithChildren) = transaction {
        resolvePhantoms(locationRepository.getChildren(location))
    }

    private fun resolvePhantoms(locations: List<Location>) = locations
        .flatMap { location ->
            if (location.isPhantom && location is Location.WithChildren) {
                locationRepository.getChildren(location)
            } else {
                emptyList()
            }
        }
}
