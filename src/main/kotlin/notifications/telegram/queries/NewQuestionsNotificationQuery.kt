package notifications.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import notifications.telegram.flows.NewQuestionsPagerData

object NewQuestionsNotificationQuery {
    @Serializable
    @SerialName("sq")
    data class SelectQuestion(val questionId: Long, val newQuestionsPagerData: NewQuestionsPagerData) : Query
}
