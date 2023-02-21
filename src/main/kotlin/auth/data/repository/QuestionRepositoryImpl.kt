package auth.data.repository

import auth.domain.repository.QuestionRepository
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.data.tables.Questions

@Single
class QuestionRepositoryImpl : QuestionRepository {
    override fun getQuestionTextByQuestionId(questionId: Long): String {
        return Questions
            .select { Questions.id eq questionId }
            .map { it[Questions.text] }[0]
    }
}
