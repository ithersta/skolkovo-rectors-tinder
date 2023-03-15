package qna.domain.repository

import kotlinx.datetime.Instant

interface AcceptedResponsesRepository {
    fun add(id: Long, time: Instant): Boolean
}
