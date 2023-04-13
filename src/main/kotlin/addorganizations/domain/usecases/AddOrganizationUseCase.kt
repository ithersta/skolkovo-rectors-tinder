package addorganizations.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import organizations.domain.entities.Organization
import organizations.domain.repository.OrganizationRepository

@Single
class AddOrganizationUseCase(
    private val organizationRepository: OrganizationRepository,
    private val transaction: Transaction
) {
    operator fun invoke(organization: String): Organization = transaction {
        return@transaction organizationRepository.add(Organization.New(organization))
    }
}
