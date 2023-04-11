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
        const val EventIsCreated = "Мероприятие добавлено в календарь! " +
                "Участникам отправлено оповещение о новом мероприятии"

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

    object RemoveEvent {
        const val ChooseEvent = "Выберите мероприятие: "
        const val Remove = "Вы действительно хотите удалить мероприятие?"
        const val SuccessfulRemove = "Мероприятие успешно удалено ✅"
        const val NotRemove = "Мероприятие не удалено ❌"
    }

    private val dateTimeFormatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.LONG)

    // TODO тут придумать сообщение для рассылки пользователям

    const val New = "📅Новое мероприятие"
    fun eventMessage(event: Event) =
        buildEntities {
            bold(event.name)
            regularln("🕓 ")
            regular(
                dateTimeFormatter.format(event.timestampBegin.toLocalDateTime()) + " - "
                        + dateTimeFormatter.format(event.timestampEnd.toLocalDateTime())
            )
            if (event.description.isNotEmpty()) {
                regularln(event.description)
            }
            regularln("🔗")
            link("Подробнее", event.url)
        }
}
