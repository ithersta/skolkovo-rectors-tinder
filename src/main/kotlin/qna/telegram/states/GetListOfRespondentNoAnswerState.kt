package qna.telegram.states

import com.ithersta.tgbotapi.pagination.PagerState
import common.telegram.DialogState
import kotlinx.serialization.Serializable
import qna.domain.entities.QuestionArea

@Serializable
data class GetListOfSubjects(
    val userId: Long,
    val area: QuestionArea,
    val pagerState: PagerState = PagerState()
) : DialogState

@Serializable
data class GetListOfRespondent(
    val userId: Long,
    val questionId: Long,
    val pagerState: PagerState = PagerState()
) : DialogState
