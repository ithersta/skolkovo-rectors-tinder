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
    private val transaction: Transaction,
    private val isAdmin: IsAdminUseCase
) {
    sealed interface Result {
        object OK : Result
        object DuplicatePhoneNumber : Result
        object PhoneNumberNotAllowed : Result
    }

    operator fun invoke(userId: Long, phoneNumber: PhoneNumber): Result = transaction {
        if (userRepository.containsUserWithPhoneNumber(phoneNumber)) {
            return@transaction Result.DuplicatePhoneNumber
        }
        if (isAdmin(userId)) {
            return@transaction Result.OK
        }
        if (phoneNumberRepository.isActive(phoneNumber).not()) {
            return@transaction Result.PhoneNumberNotAllowed
        }
        Result.OK
    }
}
