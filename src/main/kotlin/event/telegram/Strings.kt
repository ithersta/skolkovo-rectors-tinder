package event.telegram

import dev.inmo.tgbotapi.utils.*
import event.domain.entities.Event
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Strings {
    object ScheduleEvent {
        const val InputName = "Введите название мероприятия"
        const val InputBeginDateTime = "Введите дату и время начала мероприятия в формате дд.ММ.гггг чч:мм"
        const val InputEndDateTime = "Введите дату и время окончания мероприятия в формате дд.ММ.гггг чч:мм"
        const val InputDescription = "Введите краткое описание мероприятия. " +
            "\nЕсли такого не имеется, нажмите соответствующую кнопку"
        const val NoDescription = "Нет описания"
        const val InputUrl = "Введите ссылку на мероприятие"

        // TODO тут придумать сообщение
        const val EventIsCreated = "Мероприятие успешно добавлено в календарь! Участникам отправлено оповещение"

        const val EventNotCreated = "Мероприятие не создано"
        const val InvalidDataFormat = "Введён неверный формат данных. "
        fun message(event: Event) =
            buildEntities {
                bold("Название: ")
                regular(event.name)
                boldln("Дата и время начала: ")
                regular(dateTimeFormatter.format(event.timestampBegin.toLocalDateTime()))
                boldln("Дата и время окончания: ")
                regular(dateTimeFormatter.format(event.timestampEnd.toLocalDateTime()))
                if (event.description.isNotEmpty()) {
                    boldln("Краткое описание: ")
                    regular(event.description)
                }
                boldln("Ссылка: ")
                regular(event.url)
                regularln("")
                italicln("Все верно?")
            }
    }

    private val dateTimeFormatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.LONG)

    // TODO тут придумать сообщение для рассылки пользователям
    fun eventMessage(event: Event) =
        buildEntities {
            regular(event.name)
            regularln(dateTimeFormatter.format(event.timestampBegin.toLocalDateTime()))
            regularln(dateTimeFormatter.format(event.timestampEnd.toLocalDateTime()))
            if (event.description.isNotEmpty()) {
                regularln(event.description)
            }
            regularln(event.url)
            // link()
        }
}
