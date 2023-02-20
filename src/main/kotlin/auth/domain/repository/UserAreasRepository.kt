package auth.domain.repository

interface UserAreasRepository {
    fun getSubjectsByChatId(userId: Long): List<String>
}