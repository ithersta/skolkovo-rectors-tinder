package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class RegisterUserUseCase(
    private val phoneNumberIsAllowedUseCase: PhoneNumberIsAllowedUseCase,
    private val userRepository: UserRepository,
    private val transaction: Transaction,
    private val isAdminUseCase: IsAdminUseCase
) {
    sealed interface Result {
        data class OK(val userDetails: User.Details) : Result
        object DuplicatePhoneNumber : Result
        object AlreadyRegistered : Result
        object NoAreasSet : Result
    }

    operator fun invoke(userNewDetails: User.NewDetails): Result = transaction {
        if (userNewDetails.areas.isEmpty()) {
            return@transaction Result.NoAreasSet
        }
        if (userRepository.get(userNewDetails.id) != null) {
            return@transaction Result.AlreadyRegistered
        }
        when (phoneNumberIsAllowedUseCase(userNewDetails.id, userNewDetails.phoneNumber)) {
            PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber ->
                return@transaction Result.DuplicatePhoneNumber

            PhoneNumberIsAllowedUseCase.Result.OK -> {
                val userDetails = userRepository.add(userNewDetails)
                if (isAdminUseCase.invoke(userDetails.id)) {
                    userRepository.approve(userDetails.id)
                }
                return@transaction Result.OK(userDetails)
            }
        }
    }
}
