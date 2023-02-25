package qna.domain.usecase

import auth.domain.repository.UserRepository
import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Single
class GetAreasUseCase(
    private val userRepository: UserRepository,
    private val transaction: Transaction
) {
    operator fun invoke(id: Long): Set<QuestionArea> = transaction {
        return@transaction userRepository.get(id)!!.areas
    }
}