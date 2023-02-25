package oldQuestion.domain.repository

interface QuestionRepository {
    // subject, Q_ID
    fun getSubjectsByUserIdAndIsClosed(userId: Long): Map<Long, String>

    // name, phone
    fun getRespondentIdByQuestion(questionId: Long): Map<String, String>
}
