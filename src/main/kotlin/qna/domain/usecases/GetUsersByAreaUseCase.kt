package qna.domain.usecases

import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class GetUsersByAreaUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: common.domain.Transaction
) {
    operator fun invoke(questionArea: QuestionArea, userId: Long): List<Long> = transaction {
        return@transaction userAreasRepository.getUsersByArea(questionArea).filterNot { it == userId }
    }
}
