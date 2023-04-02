package qna.domain.usecases

import common.domain.Transaction
import dev.inmo.tgbotapi.extensions.utils.flatMap
import org.koin.core.annotation.Single
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

@Single
class GetNewQuestionNotificationFlowUseCase(
    private val addQuestionUseCase: AddQuestionUseCase,
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction
) {
    class Notification(
        val userId: Long,
        val question: Question
    )

    operator fun invoke() = addQuestionUseCase.newQuestionsFlow
        .flatMap { question ->
            transaction {
                questionRepository.getNotifiableUserIds(question.id!!)
            }.map { Notification(it, question) }
        }
}
