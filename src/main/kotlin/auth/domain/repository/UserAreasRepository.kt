package auth.domain.repository

interface UserAreasRepository {
    fun getSubjectsByChatId(userId: Long, offset: Long, limit: Int): Map<Long, String>
}
