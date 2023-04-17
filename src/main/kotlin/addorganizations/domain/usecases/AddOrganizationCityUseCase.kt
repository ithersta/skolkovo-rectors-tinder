package addorganizations.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import organizations.domain.repository.OrganizationRepository

@Single
class AddOrganizationCityUseCase(
    private val organizationRepository: OrganizationRepository,
    private val transaction: Transaction
) {
    operator fun invoke(cityId: Long, organizationId: Long) = transaction {
        organizationRepository.addCity(organizationId, cityId)
    }
}
