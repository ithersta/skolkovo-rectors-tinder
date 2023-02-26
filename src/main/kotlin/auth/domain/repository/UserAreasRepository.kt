package auth.domain.repository

interface UserAreasRepository {
    fun getSubjectsByChatId(userId: Long, questionArea: Int): Map<Long, String>
}