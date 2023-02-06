package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.PhoneNumberRepository
import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class RegisterUserUseCase(
    private val phoneNumberRepository: PhoneNumberRepository,
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    sealed interface Result {
        object OK : Result
        object DuplicatePhoneNumber : Result
        object PhoneNumberNotAllowed : Result
        object AlreadyRegistered : Result
        class Error(val message: String) : Result
    }

    operator fun invoke(userDetails: User.Details): Result = transaction {
        runCatching {
            if (userRepository.get(userDetails.id) != null) {
                return@transaction Result.AlreadyRegistered
            }
            if (phoneNumberRepository.isActive(userDetails.phoneNumber).not()) {
                return@transaction Result.PhoneNumberNotAllowed
            }
            if (userRepository.containsUserWithPhoneNumber(userDetails.phoneNumber)) {
                return@transaction Result.DuplicatePhoneNumber
            }
            userRepository.add(userDetails)
        }.onFailure {
            Result.Error(it.message.toString())
        }
        Result.OK
    }
}
