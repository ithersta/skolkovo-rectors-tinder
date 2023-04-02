package qna.telegram.queries

import common.telegram.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea
import qna.telegram.flows.QuestionDigestPagerData

object QuestionDigestQuery {
    @Serializable
    @SerialName("ba")
    object BackToAreas : Query

    @Serializable
    @SerialName("sa")
    data class SelectArea(
        val area: QuestionArea
    ) : Query

    @Serializable
    @SerialName("nsq")
    data class SelectQuestion(
        override val questionId: Long,
        override val returnToPage: Int,
        override val pagerData: QuestionDigestPagerData
    ) : Query, ShowQuestionQuery

    @Serializable
    @SerialName("nqb")
    data class Back(
        val page: Int,
        val pagerData: QuestionDigestPagerData
    ) : Query

    @Serializable
    @SerialName("nqa")
    data class Respond(
        override val questionId: Long,
        override val returnToPage: Int,
        override val pagerData: QuestionDigestPagerData
    ) : Query, ShowQuestionQuery

    interface ShowQuestionQuery {
        val questionId: Long
        val returnToPage: Int
        val pagerData: QuestionDigestPagerData
    }
}
