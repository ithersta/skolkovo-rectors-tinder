package auth.domain.usecases

import auth.domain.repository.UserAreasRepository
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Single
class GetUsersByAreaUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: common.domain.Transaction
) {
    // получить потенциальных ответчиков…
    // проверить на мут …
    operator fun invoke(questionArea: QuestionArea): List<Long> = transaction {
        userAreasRepository.getUsersByArea(questionArea)
    }
}