package auth.domain.repository

interface QuestionsRepository {
    fun getListOfCurrentIssues(userSubject: String): List<String>
}