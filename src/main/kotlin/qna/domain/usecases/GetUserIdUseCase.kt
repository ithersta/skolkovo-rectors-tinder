package qna.domain.usecases

import org.koin.core.annotation.Single
import qna.domain.repository.QuestionRepository

@Single
class GetUserIdUseCase (
    private val questionRepository: QuestionRepository,
    private val transaction: common.domain.Transaction
){
    operator fun invoke(questionId: Long):Long = transaction{
        return@transaction questionRepository.getUserId(questionId)
    }
}