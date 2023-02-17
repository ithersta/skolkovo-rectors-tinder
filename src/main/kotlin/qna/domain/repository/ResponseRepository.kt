package qna.domain.repository

import qna.domain.entities.Response

interface ResponseRepository {
    fun get(responseId: Long): Response?
}
