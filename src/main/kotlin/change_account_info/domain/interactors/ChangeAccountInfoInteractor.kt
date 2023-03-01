package change_account_info.domain.interactors

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

    fun changeCity(id: Long, newCity: String) = transaction {
        userRepository.changeCity(id, newCity)
    }

    fun changeJob(id: Long, newJob: String) = transaction {
        userRepository.changeJob(id, newJob)
    }

    fun changeOrganization(id: Long, newOrganization: String) = transaction {
        userRepository.changeOrganization(id, newOrganization)
    }

    fun changeActivityDescription(id: Long, newDescription: String) = transaction {
        userRepository.changeActivityDescription(id, newDescription)
    }

    fun changeAreas(id: Long, newAreas: Set<QuestionArea>) = transaction {
        userRepository.changeAreas(id, newAreas)
    }
}
