package qna.domain.usecases

import common.domain.Transaction
import org.koin.core.annotation.Single
import qna.domain.repository.ResponseRepository

@Single
class GetRespondentByResponseIdUseCase(
    private val transaction: Transaction,
    private val responseRepository: ResponseRepository,
    private val getUserDetails: GetUserDetailsUseCase
) {
    operator fun invoke(id: Long) = transaction {
        val response = responseRepository.get(id) ?: return@transaction null
        getUserDetails(response.respondentId)
    }
}
