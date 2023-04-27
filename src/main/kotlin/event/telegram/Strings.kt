package event.telegram

import dev.inmo.tgbotapi.utils.*
import event.domain.entities.Event
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import java.time.format.DateTimeFormatter

object Strings {
    const val NoEvent = "На данный момент нет актуальных мероприятий"
    fun formatInstant(instant: Instant, timeZone: TimeZone): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return formatter.format(instant.toJavaInstant().atZone(timeZone.toJavaZoneId()))
    }
    object ScheduleEvent {
        const val InputName = "Введите название мероприятия"
        const val ChooseOptionDateTime = "Выберите опцию: "
        const val InputDate = "Ввести дату мероприятия"
        const val InputDateTime = "Ввести дату и время мероприятия"
        const val InputBeginDate = "Введите дату начала мероприятия в формате дд.мм.гггг чч:мм"
        const val InputEndDate = "Введите дату окончания мероприятия в формате дд.мм.гггг чч:мм"
        const val InputBeginDateTime = "Введите дату и время начала мероприятия в формате дд.мм.гггг чч:мм"
        const val InputEndDateTime = "Введите дату и время окончания мероприятия в формате дд.мм.гггг чч:мм"
        const val InputDescription =
            "Введите краткое описание мероприятия. " + "\nЕсли такого не имеется, нажмите соответствующую кнопку"
        const val NoDescription = "Нет описания"
        const val InputUrl = "Введите ссылку на мероприятие"

        const val EventIsCreated = "Мероприятие добавлено в календарь ✅ " +
            "\nУчастникам сообщества отправлено оповещение о новом мероприятии"

        const val EventNotCreated = "❌ Мероприятие не создано"
        const val InvalidTimeInterval = "Некорректно указан временной интервал"
        const val InvalidLink = "Неверный формат ссылки"
        const val InvalidDataFormat = "Введён неверный формат данных. "
        fun approveEventMessage(event: Event, timeZone: TimeZone) = buildEntities {
            bold("Название: ")
            regular(event.name.value)
            regularln("")
            bold("Дата и время начала: ")
            regular(formatInstant(event.timestampBegin, timeZone))
            regularln("")
            bold("Дата и время окончания: ")
            regular(formatInstant(event.timestampEnd, timeZone))
            if (event.description != null) {
                regularln("")
                bold("Краткое описание: ")
                regular(event.description.value)
            }
            regularln("")
            bold("Ссылка: ")
            regular(event.url.value)
            regularln("")
            italicln("\nВсе верно?")
        }
    }

    object RemoveEvent {
        const val ChooseEvent = "Выберите мероприятие: "

        fun removeEventMessage(event: Event, timeZone: TimeZone) = buildEntities {
            addAll(eventInfo(event, timeZone))
            regular("Вы действительно хотите удалить мероприятие?")
        }

        fun removedEventMessage(event: Event, timeZone: TimeZone) = buildEntities {
            addAll(eventInfo(event, timeZone))
            regular("Мероприятие успешно удалено ✅")
        }

        fun notRemovedEventMessage(event: Event, timeZone: TimeZone) = buildEntities {
            addAll(eventInfo(event, timeZone))
            regular("❌ Мероприятие не удалено")
        }
    }

    fun newEventMessage(event: Event, timeZone: TimeZone) = buildEntities {
        regular("📨 Новое мероприятие \n\n")
        addAll(eventInfo(event, timeZone))
    }

    fun eventInfo(event: Event, timeZone: TimeZone) = buildEntities {
        boldln("📅 " + event.name)
        regular("🕓 ")
        regular(
            formatInstant(event.timestampBegin, timeZone) +
                " - " + formatInstant(event.timestampEnd, timeZone)
        )
        regularln("")
        event.description?.let { italicln(it.value) }
        regular("🔗")
        link("Ссылка", event.url.value)
        regularln("\n")
    }
}
