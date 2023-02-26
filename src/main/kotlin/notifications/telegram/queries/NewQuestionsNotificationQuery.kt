package notifications.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import notifications.domain.entities.NewQuestionsNotification

object NewQuestionsNotificationQuery {
    @Serializable
    @SerialName("nsq")
    data class SelectQuestion(
        override val questionId: Long,
        override val returnToPage: Int,
        override val newQuestionsNotification: NewQuestionsNotification
    ) : Query, ShowQuestionQuery

    @Serializable
    @SerialName("nqb")
    data class Back(
        val page: Int,
        val newQuestionsNotification: NewQuestionsNotification
    ) : Query

    @Serializable
    @SerialName("nqa")
    data class Respond(
        override val questionId: Long,
        override val returnToPage: Int,
        override val newQuestionsNotification: NewQuestionsNotification
    ) : Query, ShowQuestionQuery

    interface ShowQuestionQuery {
        val questionId: Long
        val returnToPage: Int
        val newQuestionsNotification : NewQuestionsNotification
    }
}
