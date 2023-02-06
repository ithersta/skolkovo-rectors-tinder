package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.PhoneNumberRepository
import auth.domain.repository.UserRepository
import org.koin.core.annotation.Single

@Single
class RegisterUserUseCase(
    private val phoneNumberRepository: PhoneNumberRepository,
    private val userRepository: UserRepository
) {
    sealed interface Result {
        object OK : Result
        object DuplicatePhoneNumber : Result
        object PhoneNumberNotAllowed : Result
        class Error(val message: String) : Result
    }

    operator fun invoke(userDetails: User.Details): Result {
        runCatching {
            if (phoneNumberRepository.isActive(userDetails.phoneNumber).not()) {
                return Result.PhoneNumberNotAllowed
            }
            if (userRepository.containsUserWithPhoneNumber(userDetails.phoneNumber)) {
                return Result.DuplicatePhoneNumber
            }
            userRepository.add(userDetails)
        }.onFailure {
            return Result.Error(it.message.toString())
        }
        return Result.OK
    }
}
