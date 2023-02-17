package auth.data.repository

import auth.data.tables.Users
import auth.domain.repository.QuestionsRepository
import org.jetbrains.exposed.sql.select

class QuestionsRepositoryImpl : QuestionsRepository {
    override fun getListOfCurrentIssues(): List<String> {
        Users.
    }

}