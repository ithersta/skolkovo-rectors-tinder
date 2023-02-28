package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class GetSubjectsByAreaUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long, questionArea: QuestionArea): List<Question> = transaction {
        return@transaction userAreasRepository.getByArea(userId, questionArea)
    }
}
