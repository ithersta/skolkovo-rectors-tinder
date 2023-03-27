package notifications.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import notifications.domain.entities.DelayedNewQuestionsNotification

object NewQuestionsNotificationQuery {
    @Serializable
    @SerialName("nsq")
    data class SelectQuestion(
        override val questionId: Long,
        override val returnToPage: Int,
        override val delayedNewQuestionsNotification: DelayedNewQuestionsNotification
    ) : Query, ShowQuestionQuery

    @Serializable
    @SerialName("nqb")
    data class Back(
        val page: Int,
        val delayedNewQuestionsNotification: DelayedNewQuestionsNotification
    ) : Query

    @Serializable
    @SerialName("nqa")
    data class Respond(
        override val questionId: Long,
        override val returnToPage: Int,
        override val delayedNewQuestionsNotification: DelayedNewQuestionsNotification
    ) : Query, ShowQuestionQuery

    interface ShowQuestionQuery {
        val questionId: Long
        val returnToPage: Int
        val delayedNewQuestionsNotification: DelayedNewQuestionsNotification
    }
}
