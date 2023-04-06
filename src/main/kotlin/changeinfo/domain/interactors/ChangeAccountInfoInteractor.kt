package changeinfo.domain.interactors

import auth.domain.entities.OrganizationType
import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Single
class ChangeAccountInfoInteractor(
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    fun changeName(id: Long, newName: String) = transaction {
        userRepository.changeName(id, newName)
    }

    fun changeJob(id: Long, newJob: String) = transaction {
        userRepository.changeJob(id, newJob)
    }

    fun changeOrganizationType(id: Long, newType: OrganizationType) = transaction {
        userRepository.changeOrganizationType(id, newType)
    }

    fun changeOrganization(id: Long, newOrganizationId: Long) = transaction {
        userRepository.changeOrganizationId(id, newOrganizationId)
    }

    fun changeActivityDescription(id: Long, newDescription: String) = transaction {
        userRepository.changeActivityDescription(id, newDescription)
    }

    fun changeAreas(id: Long, newAreas: Set<QuestionArea>) = transaction {
        userRepository.changeAreas(id, newAreas)
    }
}
