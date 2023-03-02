package oldquestion.data.repository

import oldquestion.domain.repository.QuestionRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.data.tables.Questions

@Single
class QuestionRepositoryImpl : QuestionRepository {
    override fun getSubjectsByUserIdAndIsClosed(userId: Long): Map<Long, String> {
        return Questions
            .select(Questions.isClosed eq true and (Questions.authorId eq userId))
            .associate { it[Questions.id].value to it[Questions.subject] }
    }
}
