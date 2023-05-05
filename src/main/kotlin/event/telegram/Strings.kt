package event.telegram

import dev.inmo.tgbotapi.utils.*
import event.domain.entities.Event
import kotlinx.datetime.*
import java.time.format.DateTimeFormatter

object Strings {
    private val midnight = LocalTime(0, 0)

    const val NoEvent = "На данный момент нет актуальных мероприятий"
    fun formatInstant(instant: Instant, timeZone: TimeZone): String {
        return if (instant.toLocalDateTime(timeZone).time == midnight) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            formatter.format(instant.toJavaInstant().atZone(timeZone.toJavaZoneId()))
        } else {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            formatter.format(instant.toJavaInstant().atZone(timeZone.toJavaZoneId()))
        }
    }

    object ScheduleEvent {
        const val InputName = "Введите название мероприятия"
        const val InputBeginDateTime = "Введите дату или дату и время начала мероприятия в формате дд.мм.гггг чч:мм"
        const val InputEndDate = "Введите дату окончания мероприятия в формате дд.мм.гггг"
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
            val timeBegin = formatInstant(event.timestampBegin, timeZone)
            val timeEnd = formatInstant(event.timestampEnd, timeZone)
            val midnight = LocalTime(0, 0)
            if (event.timestampBegin.toLocalDateTime(timeZone).time == midnight &&
                event.timestampEnd.toLocalDateTime(timeZone).time == midnight
            ) {
                bold("Дата начала: ")
                regular(timeBegin)
                regularln("")
                bold("Дата окончания: ")
                regular(timeEnd)
            } else {
                bold("Дата и время начала: ")
                regular(timeBegin)
                regularln("")
                bold("Дата и время окончания: ")
                regular(timeEnd)
            }
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
        regular("🕓 ")
        val timeBegin = formatInstant(event.timestampBegin, timeZone)
        val timeEnd = formatInstant(event.timestampEnd, timeZone)
        val beginLocalDateTime = event.timestampBegin.toLocalDateTime(timeZone)
        val endLocalDateTime = event.timestampBegin.toLocalDateTime(timeZone)
        if (beginLocalDateTime.time == midnight && endLocalDateTime.time == midnight) {
            regular(
                timeBegin +
                    " - " + timeEnd
            )
        } else if (beginLocalDateTime.date == endLocalDateTime.date) {
            regular(
                timeBegin +
                    " - " + timeEnd.toLocalDateTime().date
            )
        } else {
            regular(
                timeBegin +
                    " - " + timeEnd
            )
        }
        regularln("")
        boldln("📅 " + event.name.value)
        event.description?.let { italicln(it.value) }
        regular("🔗")
        link("Ссылка", event.url.value)
        regularln("\n")
    }
}
