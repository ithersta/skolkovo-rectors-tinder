package auth.domain.repository

import auth.domain.entities.PhoneNumber

interface PhoneNumberRepository {
    fun addAll(phoneNumbers: Collection<PhoneNumber>)
    fun isActive(phoneNumber: PhoneNumber): Boolean
}
