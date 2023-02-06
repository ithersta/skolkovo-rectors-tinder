package auth.domain.repository

import auth.domain.entities.PhoneNumber
import auth.domain.entities.User

interface UserRepository {
    fun add(user: User.Details)
    fun get(id: Long): User.Details?
    fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean
}
