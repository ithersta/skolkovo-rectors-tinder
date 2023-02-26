package notifications.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.utils.row
import generated.RoleFilterBuilder
import generated.dataButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import notifications.domain.usecases.GetNewQuestionsNotificationFlowUseCase
import notifications.domain.usecases.GetQuestionsBetweenUseCase
import notifications.telegram.queries.NewQuestionsNotificationQuery
import org.koin.core.component.inject

@Serializable
class NewQuestionsPagerData(
    val from: Instant,
    val until: Instant,
    val excludeUserId: Long
)

fun RoleFilterBuilder<User.Normal>.newQuestionsNotificationFlow(): InlineKeyboardPager<NewQuestionsPagerData> {
    val getQuestionsBetween: GetQuestionsBetweenUseCase by inject()
    val newQuestionsPager = pager(id = "new_questions", dataKClass = NewQuestionsPagerData::class) {
        val questions = getQuestionsBetween(data.from, data.until, data.excludeUserId, limit, offset)
        inlineKeyboard {
            questions.slice.forEach { question ->
                row {
                    dataButton(question.subject, NewQuestionsNotificationQuery.SelectQuestion(question.id!!, data))
                }
            }
            navigationRow(questions.count)
        }
    }
    anyState {
    }

    return newQuestionsPager
}
