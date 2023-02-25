package notifications.telegram.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.pagination.InlineKeyboardPager
import com.ithersta.tgbotapi.pagination.pager
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import generated.RoleFilterBuilder
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class NewQuestionsPagerData(
    val from: Instant,
    val until: Instant,
    val excludeUserId: Long
)

fun RoleFilterBuilder<User.Normal>.newQuestionsNotificationFlow(): InlineKeyboardPager<NewQuestionsPagerData> {
    val newQuestionsPager = pager(id = "newQuestions", dataKClass = NewQuestionsPagerData::class) {
        inlineKeyboard {
            navigationRow()
        }
    }
    anyState {

    }
    return newQuestionsPager
}
