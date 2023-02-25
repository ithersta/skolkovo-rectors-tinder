package auth.domain.repository

import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import qna.domain.entities.QuestionArea

interface UserRepository {
    fun add(user: User.Details)
    fun get(id: Long): User.Details?
    fun isRegistered(id: Long): Boolean
    fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean

    fun changeName(id: Long, newName: String)

    fun changeCity(id:Long, newCity: String)

    fun changeJob(id: Long, newJob: String)

    fun changeOrganization(id: Long, newOrganization: String)

    fun changeAreas(id: Long, newArea: Set<QuestionArea>)

    fun changeActivityDescription(id: Long, newActivityDescription: String)
}
