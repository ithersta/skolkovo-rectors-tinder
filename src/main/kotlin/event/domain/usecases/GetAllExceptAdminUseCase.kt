package event.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class GetAllExceptAdminUseCase(
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long): List<User.Details?> = transaction {
        return@transaction userRepository.getAllExceptUser(id)
    }
}
