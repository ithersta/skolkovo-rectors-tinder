package qna.data.repository

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.insertIgnore
import org.koin.core.annotation.Single
import qna.data.tables.AcceptedResponses
import qna.domain.repository.AcceptedResponsesRepository

@Single
class AcceptedResponsesRepositoryImpl : AcceptedResponsesRepository {
    override fun add(id: Long, time: Instant) =
        AcceptedResponses.insertIgnore {
            it[responseId] = id
            it[at] = time
        }.insertedCount != 0
}
