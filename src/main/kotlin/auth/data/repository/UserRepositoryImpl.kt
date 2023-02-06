package auth.data.repository

import auth.data.tables.Users
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.repository.UserRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl : UserRepository {
    override fun add(user: User.Details) {
        Users.insert {
            it[phoneNumber] = user.phoneNumber.value
            it[city] = user.city
            it[job] = user.job
            it[organization] = user.organization
            it[professionalAreas] = user.professionalAreas
            it[activityDescription] = user.activityDescription
        }
    }

    override fun get(id: Long): User.Details? {
        return Users.select { Users.id eq id }.firstOrNull()?.let {
            User.Details(
                id = it[Users.id].value,
                phoneNumber = PhoneNumber.of(it[Users.phoneNumber])!!,
                city = it[Users.city],
                professionalAreas = it[Users.professionalAreas],
                job = it[Users.job],
                organization = it[Users.organization],
                activityDescription = it[Users.activityDescription]
            )
        }
    }

    override fun containsUserWithPhoneNumber(phoneNumber: PhoneNumber): Boolean {
        return Users.select { Users.phoneNumber eq phoneNumber.value }.firstOrNull() != null
    }
}
