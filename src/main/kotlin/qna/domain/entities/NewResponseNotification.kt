package qna.domain.entities

import kotlinx.serialization.Serializable

@Serializable
class NewResponseNotification(
    val questionAuthorId: Long
)
