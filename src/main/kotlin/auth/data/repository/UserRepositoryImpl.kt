package auth.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.repository.UserRepository
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Single
class UserRepositoryImpl : UserRepository {
    override fun add(user: User.Details) {
        Users.insert {
            it[id] = user.id
            it[phoneNumber] = user.phoneNumber.value
            it[name] = user.name
            it[city] = user.city
            it[job] = user.job
            it[organization] = user.organization
            it[activityDescription] = user.activityDescription
        }
        UserAreas.batchInsert(user.areas) {
            this[UserAreas.userId] = user.id
            this[UserAreas.area] = it
        }
    }

    override fun get(id: Long): User.Details? {
        val areas: Set<QuestionArea> = UserAreas.select { UserAreas.userId eq id }.map { it[UserAreas.area] }.toSet()
        return Users.select { Users.id eq id }.firstOrNull()?.let {
            val phoneNumber = PhoneNumber.of(it[Users.phoneNumber])
            checkNotNull(phoneNumber)
            User.Details(
                id = it[Users.id].value,
                phoneNumber = phoneNumber,
                name = it[Users.name],
                city = it[Users.city],
                job = it[Users.job],
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
}
