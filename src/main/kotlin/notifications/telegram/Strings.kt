package notifications.telegram

import dev.inmo.tgbotapi.utils.boldln
import dev.inmo.tgbotapi.utils.buildEntities
import dev.inmo.tgbotapi.utils.regularln
import notifications.domain.entities.NotificationPreference
import qna.domain.entities.Question

object Strings {
    fun question(question: Question) = buildEntities {
        boldln(question.subject)
        regularln(question.text)
    }

    fun respondedQuestion(question: Question) = buildEntities {
        regularln("✅Вы согласились ответить на вопрос:")
        regularln("")
        addAll(question(question))
    }

    fun NotificationPreference.localizedString() = when (this) {
        NotificationPreference.RightAway -> "Сразу"
        NotificationPreference.Daily -> "Ежедневно"
        NotificationPreference.Weekly -> "Еженедельно"
    }

    fun newQuestionsMessage(notificationPreference: NotificationPreference) = when (notificationPreference) {
        NotificationPreference.RightAway -> error("Impossible")
        NotificationPreference.Daily -> "Новые вопросы за день"
        NotificationPreference.Weekly -> "Новые вопросы за неделю"
    }

    object Main {
        const val Message = "Выберите, как часто Вы хотите получать уведомления о новых вопросах"
        const val TurnOn = "Включить"
        const val TurnOff = "Выключить"
    }

    object Buttons {
        const val Respond = "Ответить"
    }
}
