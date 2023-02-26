package notifications.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.utils.row
import generated.RoleFilterBuilder
import generated.dataButton
import notifications.domain.entities.NewQuestionsNotification
import notifications.domain.usecases.GetQuestionsBetweenUseCase
import notifications.telegram.queries.NewQuestionsNotificationQuery
import org.koin.core.component.inject

fun RoleFilterBuilder<User.Normal>.newQuestionsNotificationFlow(): InlineKeyboardPager<NewQuestionsNotification> {
    val getQuestionsBetween: GetQuestionsBetweenUseCase by inject()
    val newQuestionsPager = pager(id = "new_questions", dataKClass = NewQuestionsNotification::class) {
        val questions = getQuestionsBetween(
            from = data.from, until = data.until, excludeUserId = data.userId, limit = limit, offset = offset
        )
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
