package auth.domain.repository

import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import qna.domain.entities.QuestionArea

@Suppress("TooManyFunctions")
interface UserRepository {
    fun add(user: User.NewDetails): User.Details
    fun get(id: Long): User.Details?
    fun getAllActiveExceptUser(id: Long): List<Long>
    fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean
    fun changeName(id: Long, newName: User.Name)
    fun changeCityId(id: Long, newCityId: Long)
    fun changeJob(id: Long, newJob: User.Job)
    fun changeOrganizationType(id: Long, newType: OrganizationType)
    fun changeOrganizationId(id: Long, newOrganizationId: Long)
    fun changeAreas(id: Long, newArea: Set<QuestionArea>)
    fun changeActivityDescription(id: Long, newActivityDescription: User.ActivityDescription)
    fun approve(id: Long)
    fun delete(id: Long)
}
