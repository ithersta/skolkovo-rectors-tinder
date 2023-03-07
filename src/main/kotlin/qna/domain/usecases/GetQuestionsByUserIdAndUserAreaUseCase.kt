package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class GetQuestionsByUserIdAndUserAreaUseCase(
    private val userAreasRepository: UserAreasRepository,
    private val transaction: Transaction
) {
    operator fun invoke(userId: Long, userArea: QuestionArea): List<Question> = transaction {
        return@transaction userAreasRepository.getSubjectsByUserId(userId, userArea)
    }
}
