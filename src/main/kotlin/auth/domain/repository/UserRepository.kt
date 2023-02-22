package auth.domain.repository

import auth.domain.entities.PhoneNumber
import auth.domain.entities.User

interface UserRepository {
    fun add(user: User.Details)
    fun get(id: Long): User.Details?
    fun isRegistered(id: Long): Boolean
    fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean
    fun getPhoneNumber(id: Long): String
    fun getFirstName(id:Long) : String
}
