package auth.data.repository

import auth.data.tables.PhoneNumbers
import auth.domain.entities.PhoneNumber
import auth.domain.repository.PhoneNumberRepository
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.batchReplace
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single

@Single
class PhoneNumberRepositoryImpl : PhoneNumberRepository {
    override fun addAll(phoneNumbers: Collection<PhoneNumber>) {
        PhoneNumbers.batchReplace(phoneNumbers) {
            this[PhoneNumbers.phoneNumber] = it.value
            this[PhoneNumbers.isActive] = true
        }
    }

    override fun isActive(phoneNumber: PhoneNumber): Boolean {
        return PhoneNumbers.select { PhoneNumbers.phoneNumber eq phoneNumber.value }
            .firstOrNull()?.get(PhoneNumbers.isActive) ?: false
    }

    override fun contains(phoneNumber: PhoneNumber): Boolean {
        return PhoneNumbers.select { PhoneNumbers.phoneNumber eq phoneNumber.value }.empty().not()
    }
}
