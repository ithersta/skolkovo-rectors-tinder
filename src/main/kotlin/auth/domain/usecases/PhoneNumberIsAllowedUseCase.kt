package auth.domain.usecases

import auth.domain.entities.PhoneNumber
import auth.domain.repository.PhoneNumberRepository
import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single

@Single
class PhoneNumberIsAllowedUseCase(
    private val phoneNumberRepository: PhoneNumberRepository,
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    sealed interface Result {
        object OK : Result
        object DuplicatePhoneNumber : Result
        object PhoneNumberNotAllowed : Result
    }

    operator fun invoke(phoneNumber: PhoneNumber): PhoneNumberIsAllowedUseCase.Result = transaction {
        if (userRepository.containsUserWithPhoneNumber(phoneNumber)) {
            return@transaction PhoneNumberIsAllowedUseCase.Result.DuplicatePhoneNumber
        }
        if (phoneNumberRepository.isActive(phoneNumber).not()) {
            return@transaction PhoneNumberIsAllowedUseCase.Result.PhoneNumberNotAllowed
        }
        PhoneNumberIsAllowedUseCase.Result.OK
    }
}

