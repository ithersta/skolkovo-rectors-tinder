package auth.domain.repository

import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import qna.domain.entities.QuestionArea

interface UserRepository {
    fun add(user: User.Details)
    fun get(id: Long): User.Details?
    fun isRegistered(id: Long): Boolean
    fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean
    fun getAreasByChatId(id:Long) : Set<QuestionArea>
}
