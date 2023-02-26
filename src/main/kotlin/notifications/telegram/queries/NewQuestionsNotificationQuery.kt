package notifications.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import notifications.domain.entities.NewQuestionsNotification

object NewQuestionsNotificationQuery {
    @Serializable
    @SerialName("nsq")
    data class SelectQuestion(val questionId: Long, val newQuestionsNotification: NewQuestionsNotification) : Query
}
