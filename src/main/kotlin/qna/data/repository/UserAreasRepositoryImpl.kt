package qna.data.repository

import auth.data.tables.UserAreas
import mute.data.tables.MuteSettings
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository
import java.util.stream.Collectors

@Single
class UserAreasRepositoryImpl : UserAreasRepository {

    // тут не знаю, как лучше сделать....
    private fun getById(questionId: Long): Question {
        val areas = QuestionAreas
            .select { QuestionAreas.questionId eq questionId }
            .map { it[QuestionAreas.area] }.toSet()
        return Questions.select { Questions.id eq questionId }.map {
            Question(
                authorId = it[Questions.authorId].value,
                intent = it[Questions.intent],
                subject = it[Questions.subject],
                text = it[Questions.text],
                isClosed = it[Questions.isClosed],
                areas = areas,
                id = it[Questions.id].value
            )
        }.first()
    }

    override fun getUsersByArea(questionArea: QuestionArea): List<Long> {
        val muteUsers = MuteSettings.slice(MuteSettings.userId).selectAll()
        return UserAreas
            .slice(UserAreas.userId)
            .select { UserAreas.area eq questionArea }
            .except(muteUsers)
            .map { it[UserAreas.userId].value }
    }

    override fun getByArea(userId: Long, questionArea: QuestionArea): List<Question> {
        val questionsId = (Questions innerJoin QuestionAreas)
            .select(
                (Questions.authorId eq userId)
                    and (Questions.isClosed.eq(false))
                    and (QuestionAreas.area eq questionArea)
            )
            .map { it[QuestionAreas.questionId].value }
        return questionsId.stream()
            .map { getById(it) }
            .collect(Collectors.toList())
    }
}
