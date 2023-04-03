package auth.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import auth.data.tables.toDomainModel
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
    override fun add(user: User.NewDetails) {
        Users.insert {
            it[id] = user.id
            it[phoneNumber] = user.phoneNumber.value
            it[course] = user.course
            it[name] = user.name
            it[job] = user.job
            it[organizationType] = user.organizationType
            it[organizationId] = user.organizationId
            it[activityDescription] = user.activityDescription
        }
        UserAreas.batchInsert(user.areas) {
            this[UserAreas.userId] = user.id
            this[UserAreas.area] = it
        }
    }

    override fun get(id: Long): User.Details? {
        return Users.Entity.findById(id)?.toDomainModel()
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

    override fun changeOrganizationId(id: Long, newOrganizationId: Long) {
        Users.update({ Users.id eq id }) {
            it[organizationId] = newOrganizationId
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
