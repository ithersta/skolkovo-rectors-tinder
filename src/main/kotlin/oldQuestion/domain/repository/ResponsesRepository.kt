package oldQuestion.domain.repository

interface ResponsesRepository {
    // name, phone
    fun getRespondentIdByQuestion(questionId: Long): Map<String, String>
}