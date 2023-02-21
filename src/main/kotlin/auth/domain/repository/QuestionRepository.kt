package auth.domain.repository

import auth.domain.entities.User

interface QuestionRepository {
    fun getQuestionTextByQuestionId(questionId: Long): String
    fun getUserInfoByQuestionId(questionId: Long): User.Details
}
