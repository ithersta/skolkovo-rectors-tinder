package auth.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import auth.domain.entities.OrganizationType
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.repository.UserRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Suppress("TooManyFunctions")
@Single
class UserRepositoryImpl : UserRepository {
    override fun add(user: User.Details) {
        Users.insert {
            it[id] = user.id
            it[phoneNumber] = user.phoneNumber.value
            it[course] = user.course
            it[name] = user.name
            it[city] = user.city
            it[job] = user.job
            it[organizationType] = user.organizationType
            it[organization] = user.organization
            it[activityDescription] = user.activityDescription
        }
        UserAreas.batchInsert(user.areas) {
            this[UserAreas.userId] = user.id
            this[UserAreas.area] = it
        }
    }

    override fun get(id: Long): User.Details? {
        val areas = UserAreas.select { UserAreas.userId eq id }.map { it[UserAreas.area] }.toSet()
        return Users.select { Users.id eq id }.firstOrNull()?.let {
            val phoneNumber = PhoneNumber.of(it[Users.phoneNumber])
            checkNotNull(phoneNumber)
            User.Details(
                id = it[Users.id].value,
                phoneNumber = phoneNumber,
                course = it[Users.course],
                name = it[Users.name],
                city = it[Users.city],
                job = it[Users.job],
                organizationType = it[Users.organizationType],
                organization = it[Users.organization],
                activityDescription = it[Users.activityDescription],
                areas = areas
            )
        }
    }

    override fun isRegistered(id: Long): Boolean {
        return Users.select { Users.id eq id }.empty().not()
    }

    override fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean {
        return Users.select { Users.phoneNumber eq phoneNumber.value }.empty().not()
    }

    override fun changeName(id: Long, newName: String) {
        Users.update({ Users.id eq id }) {
            it[name] = newName
        }
    }

    override fun changeCity(id: Long, newCity: String) {
        Users.update({ Users.id eq id }) {
            it[city] = newCity
        }
    }

    override fun changeJob(id: Long, newJob: String) {
        Users.update({ Users.id eq id }) {
            it[job] = newJob
        }
    }

    override fun changeOrganizationType(id: Long, newType: OrganizationType) {
        Users.update({ Users.id eq id }) {
            it[organizationType] = newType
        }
    }

    override fun changeOrganization(id: Long, newOrganization: String) {
        Users.update({ Users.id eq id }) {
            it[organization] = newOrganization
        }
    }

    override fun changeAreas(id: Long, newArea: Set<QuestionArea>) {
        UserAreas.deleteWhere { userId eq id }
        UserAreas.batchInsert(newArea) {
            this[UserAreas.userId] = id
            this[UserAreas.area] = it
        }
    }

    override fun changeActivityDescription(id: Long, newActivityDescription: String) {
        Users.update({ Users.id eq id }) {
            it[activityDescription] = newActivityDescription
        }
    }
}
