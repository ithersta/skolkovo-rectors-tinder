package qna.domain.usecases

import auth.data.repository.MuteSettingsRepositoryImpl
import auth.domain.repository.UserAreasRepository
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Single
class GetUsersByAreaUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val muteSettingsRepositoryImpl: MuteSettingsRepositoryImpl,
    private val transaction: common.domain.Transaction
) {
    private fun getUniqueElements(sheet1: List<Long>, sheet2: List<Long>): List<Long> {
        val uniqueElements = mutableListOf<Long>()
        for (element in sheet1) {
            if (!sheet2.contains(element)) {
                uniqueElements.add(element)
            }
        }
        return uniqueElements
    }

    operator fun invoke(questionArea: QuestionArea, userId: Long): List<Long> = transaction {
        // потом убрать функцию
        val mute = muteSettingsRepositoryImpl.getAll()
        val all = userAreasRepository.getUsersByArea(questionArea, userId).filterNot { it == userId }
        return@transaction getUniqueElements(all, mute).toSet().toList()
    }
}
