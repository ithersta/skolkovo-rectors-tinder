package qna.domain.usecases

import common.domain.Transaction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.Clock
import org.koin.core.annotation.Single
import qna.domain.entities.HideFrom
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.entities.QuestionIntent
import qna.domain.repository.QuestionRepository

@Single
class AddQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val transaction: Transaction,
    private val clock: Clock
) {
    private val _newQuestionsFlow = MutableSharedFlow<Question>()
    val newQuestionsFlow = _newQuestionsFlow.asSharedFlow()

    suspend operator fun invoke(
        authorId: Long,
        intent: QuestionIntent,
        subject: String,
        text: String,
        areas: Set<QuestionArea>,
        hideFrom: HideFrom
    ): Question = transaction {
        val question = Question(authorId, intent, subject, text, false, areas, clock.now(), hideFrom)
        val id = questionRepository.add(question)
        question.copy(id = id)
    }.also {
        _newQuestionsFlow.emit(it)
    }
}
