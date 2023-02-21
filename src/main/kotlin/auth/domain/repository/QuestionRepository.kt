package auth.domain.repository

interface QuestionRepository {
    fun getQuestionTextByQuestionId(questionId: Long): String
}
