package qna.domain.usecases

import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

class AddQuestionUseCase(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(questionDetails: Question.Details) {
        questionRepository.add(questionDetails)
    }
}
