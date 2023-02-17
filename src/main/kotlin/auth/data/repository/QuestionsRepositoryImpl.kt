package auth.data.repository

import auth.domain.repository.QuestionsRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import qna.data.tables.Questions

class QuestionsRepositoryImpl : QuestionsRepository {
    override fun getListOfCurrentIssues(userSubject: String): List<String> {
        return Questions.slice(Questions.text)
            .select { (Questions.isClosed eq false) and (Questions.subject eq userSubject) }
            .map {  it[Questions.text] }

    }

}