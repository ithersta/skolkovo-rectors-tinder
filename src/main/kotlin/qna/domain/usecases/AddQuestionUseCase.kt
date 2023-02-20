package qna.domain.usecases

import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

@Single
class AddQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: common.domain.Transaction
) {
    operator fun invoke (questionDetails: Question.Details) = transaction{
        return@transaction questionRepository.add(questionDetails)
    }
}
