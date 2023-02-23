package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

@Single
class AddQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    operator fun invoke(questionDetails: Question): Question = transaction {
        val id = questionRepository.add(questionDetails)
        questionDetails.copy(id = id)
    }
}
