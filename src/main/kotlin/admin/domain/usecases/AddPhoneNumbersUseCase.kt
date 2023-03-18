package admin.domain.usecases

import auth.domain.entities.PhoneNumber
import auth.domain.repository.PhoneNumberRepository
import common.domain.Transaction
import org.koin.core.annotation.Single
import java.util.LinkedList

@Single
class AddPhoneNumbersUseCase(
    private val phoneNumbersRepository: PhoneNumberRepository,
    private val transaction: Transaction
) {
    sealed interface Result {
        object OK : Result
        class PhoneNumberNotAllowed(
            val listOfNotActive: List<PhoneNumber>
        ) : Result
    }

    operator fun invoke(phoneNumbers: Collection<PhoneNumber>): Result = transaction {
        val listOfNotActive = LinkedList<PhoneNumber>()
        phoneNumbers.forEach { phoneNumber ->
            if (phoneNumbersRepository.contains(phoneNumber) && phoneNumbersRepository.isActive(phoneNumber).not()) {
                listOfNotActive.add(phoneNumber)
            }
        }
        if (listOfNotActive.isEmpty()) {
            val notContainsPhoneNumbers = phoneNumbers.filter { phoneNumbersRepository.contains(it).not() }
            phoneNumbersRepository.addAll(notContainsPhoneNumbers)
            Result.OK
        } else {
            Result.PhoneNumberNotAllowed(listOfNotActive)
        }
    }
}
