package qna.domain.usecases

import auth.domain.entities.User
import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class GetFilteredUsersByAreaUseCase(
    private val userAreasRepository: UserAreasRepository, private val transaction: Transaction
) {
    operator fun invoke(questionArea: QuestionArea, user: User.Details): List<Long> = transaction {
        return@transaction userAreasRepository.getFilteredUsersByArea(questionArea, user.city)
            .filterNot { it == user.id }
    }
}