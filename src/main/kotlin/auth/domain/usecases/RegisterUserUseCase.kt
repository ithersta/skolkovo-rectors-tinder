package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class RegisterUserUseCase(
    private val phoneNumberIsAllowedUseCase: PhoneNumberIsAllowedUseCase,
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    sealed interface Result {
        object OK : Result
        object DuplicatePhoneNumber : Result
        object AlreadyRegistered : Result
        object NoAreasSet : Result
    }

    operator fun invoke(userDetails: User.NewDetails): Result = transaction {
        if (userDetails.areas.isEmpty()) {
            return@transaction Result.NoAreasSet
        }
        if (userRepository.isRegistered(userDetails.id)) {
            return@transaction Result.AlreadyRegistered
        }
        when (phoneNumberIsAllowedUseCase(userDetails.id, userDetails.phoneNumber)) {
            PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber ->
                return@transaction Result.DuplicatePhoneNumber

            PhoneNumberIsAllowedUseCase.Result.OK -> {
                userRepository.add(userDetails)
                return@transaction Result.OK
            }
        }
    }
}
