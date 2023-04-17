package addorganizations.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import organizations.domain.entities.Organization
import organizations.domain.repository.OrganizationRepository

@Single
class GetOrganizationByIdUseCase(
    private val organizationRepository: OrganizationRepository,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long): Organization? = transaction {
        return@transaction organizationRepository.get(id)
    }
}
