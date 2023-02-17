package auth.domain.repository

interface QuestionsRepository {
    fun getListOfCurrentIssues(): List<String>
}