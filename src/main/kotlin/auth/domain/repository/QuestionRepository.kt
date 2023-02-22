package auth.domain.repository

interface QuestionRepository {
    fun getQuestionTextByQuestionId(questionId: Long): String
    fun getUserIdByQuestionId(questionId: Long): Long
}
