package event.telegram

import dev.inmo.tgbotapi.utils.buildEntities
import dev.inmo.tgbotapi.utils.link
import dev.inmo.tgbotapi.utils.regular
import dev.inmo.tgbotapi.utils.regularln
import event.domain.entities.Event
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Strings {
    object ScheduleEvent {
        const val InputName = "Введите название мероприятия"
        const val InputBeginDateTime = "Введите дату и время начала мероприятия в формате дд\\.ММ\\.гггг чч:мм"
        const val InputEndDateTime = "Введите дату и время окончания мероприятия в формате дд\\.ММ\\.гггг чч:мм"
        const val InputDescription = "Введите краткое описание мероприятия"
        const val InputUrl = "Введите ссылку на мероприятие"
        const val EventIsCreated = "Мероприятие успешно добавлено!"

        const val EventNotCreated = "Мероприятие не создано"
        const val InvalidDataFormat = "Введён неверный формат данных"
    }

    private val dateTimeFormatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.LONG)

    fun message(event: Event) =
        buildEntities {
            regular(event.name)
            regularln(dateTimeFormatter.format(event.timestampBegin.toLocalDateTime()))
            regularln(dateTimeFormatter.format(event.timestampEnd.toLocalDateTime()))
            event.description?.let { regularln(it) }
            regularln(event.url)
            //link()
            regularln("")
            regularln("Все верно?")
        }
}
