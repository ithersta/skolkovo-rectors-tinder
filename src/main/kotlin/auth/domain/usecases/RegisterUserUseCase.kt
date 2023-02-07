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
        object AlreadyRegistered : Result
        object PhoneNumberNotAllowed : Result
        object NoAreasSet : Result
        class Error(val message: String) : Result
    }

    operator fun invoke(userDetails: User.Details): Result = transaction {
        if (userDetails.areas.isEmpty()) {
            return@transaction Result.NoAreasSet
        }
        runCatching {
            if (userRepository.isRegistered(userDetails.id)) {
                return@transaction Result.AlreadyRegistered
            }
            if (userRepository.containsUserWithPhoneNumber(userDetails.phoneNumber)) {
                return@transaction Result.DuplicatePhoneNumber
            }
            if (phoneNumberRepository.isActive(userDetails.phoneNumber).not()) {
                return@transaction Result.PhoneNumberNotAllowed
            }
            userRepository.add(userDetails)
        }.onFailure {
            Result.Error(it.message.toString())
        }
        Result.OK
    }
}
