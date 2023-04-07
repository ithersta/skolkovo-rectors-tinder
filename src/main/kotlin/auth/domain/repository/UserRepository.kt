package auth.domain.repository

import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import qna.domain.entities.QuestionArea

@Suppress("TooManyFunctions")
interface UserRepository {
    fun add(user: User.NewDetails)
    fun get(id: Long): User.Details?
    fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean
    fun changeName(id: Long, newName: String)
    fun changeCityId(id: Long, cityId: Long)
    fun changeJob(id: Long, newJob: String)
    fun changeOrganizationType(id: Long, newType: OrganizationType)
    fun changeOrganizationId(id: Long, newOrganizationId: Long)
    fun changeAreas(id: Long, newArea: Set<QuestionArea>)
    fun changeActivityDescription(id: Long, newActivityDescription: String)
}
