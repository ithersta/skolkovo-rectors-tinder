package auth.domain.repository

import auth.domain.entities.PhoneNumber
import auth.domain.entities.User

interface UserRepository {
    fun add(user: User.Details)
    fun get(id: Long): User.Details?
    fun getPhoneNumber(id: Long): String
    fun isRegistered(id: Long): Boolean
    fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean
}
