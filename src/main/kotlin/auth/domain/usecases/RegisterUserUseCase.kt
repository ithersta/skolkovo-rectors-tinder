package auth.domain.usecases

import auth.domain.entities.User
import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.jetbrains.exposed.sql.update
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

    operator fun invoke(userDetails: User.NewDetails): Result = transaction {

        if (userDetails.areas.isEmpty()) {
            return@transaction Result.NoAreasSet
        }
        if (userRepository.get(userDetails.id) != null) {
            return@transaction Result.AlreadyRegistered
        }
        when (phoneNumberIsAllowedUseCase(userDetails.id, userDetails.phoneNumber)) {
            PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber ->
                return@transaction Result.DuplicatePhoneNumber

            PhoneNumberIsAllowedUseCase.Result.OK -> {
                val userDetails = userRepository.add(userDetails)
                if (isAdminUseCase.invoke(userDetails.id)){
                    userRepository.approve(userDetails.id)
                }
                return@transaction Result.OK(userDetails)
            }
        }

    }
}
