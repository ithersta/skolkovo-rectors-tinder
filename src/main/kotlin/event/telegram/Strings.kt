package event.telegram

import dev.inmo.tgbotapi.utils.*
import event.domain.entities.Event
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Strings {
    object ScheduleEvent {
        const val InputName = "Введите название мероприятия"
        const val InputBeginDateTime = "Введите дату и время начала мероприятия в формате дд.ММ.гггг чч:мм"
        const val InputEndDateTime = "Введите дату и время окончания мероприятия в формате дд.ММ.гггг чч:мм"
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
        fun message(event: Event) = buildEntities {
            bold("Название: ")
            regular(event.name)
            regularln("")
            bold("Дата и время начала: ")
            regular(formatInstant(event.timestampBegin))
            regularln("")
            bold("Дата и время окончания: ")
            regular(formatInstant(event.timestampEnd))
            if (event.description != null) {
                regularln("")
                bold("Краткое описание: ")
                regular(event.description)
            }
            regularln("")
            bold("Ссылка: ")
            regular(event.url)
            regularln("")
            italicln("\nВсе верно?")
        }
    }

    const val NoEvent = "На данный момент нет актуальных мероприятий"
    object RemoveEvent {
        const val ChooseEvent = "Выберите мероприятие: "

        fun removeEventMessage(event: Event) = buildEntities {
            regular("📅 ")
            bold(event.name)
            regular("\n🕓 ")
            regular(
                formatInstant(event.timestampBegin) +
                    " - " + formatInstant(event.timestampEnd)
            )
            regularln("")
            event.description?.let { italicln(it) }
            regular("🔗")
            link("Ссылка", event.url)
            regularln("\n")
            regular("Вы действительно хотите удалить мероприятие?")
        }

        fun removedEventMessage(event: Event) = buildEntities {
            regular("📅 ")
            strikethrough(event.name)
            regular("\n🕓 ")
            regular(
                formatInstant(event.timestampBegin) +
                    " - " + formatInstant(event.timestampEnd)
            )
            regularln("")
            event.description?.let { italicln(it) }
            regular("🔗")
            link("Ссылка", event.url)
            regularln("\n")
            regular("Мероприятие успешно удалено ✅")
        }

        fun notRemovedEventMessage(event: Event) = buildEntities {
            regular("📅 ")
            bold(event.name)
            regular("\n🕓 ")
            regular(
                formatInstant(event.timestampBegin) +
                    " - " + formatInstant(event.timestampEnd)
            )
            regularln("")
            event.description?.let { italicln(it) }
            regular("🔗")
            link("Ссылка", event.url)
            regularln("\n")
            regular("❌ Мероприятие не удалено")
        }
    }

    fun formatInstant(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return formatter.format(instant.toJavaInstant().atZone(ZoneId.systemDefault()))
    }

    fun newEventMessage(event: Event) = buildEntities {
        regular("📨 Новое мероприятие ")
        boldln("\n\n📅 " + event.name)
        regular("🕓 ")
        regular(
            formatInstant(event.timestampBegin) +
                " - " + formatInstant(event.timestampEnd)
        )
        regularln("")
        event.description?.let { italicln(it) }
        regular("🔗")
        link("Ссылка", event.url)
    }

    fun eventMessage(event: Event) = buildEntities {
        regular("📅 ")
        bold(event.name)
        regular("\n🕓 ")
        regular(
            formatInstant(event.timestampBegin) +
                " - " + formatInstant(event.timestampEnd)
        )
        regularln("")
        event.description?.let { italicln(it) }
        regular("🔗")
        link("Ссылка", event.url)
        regularln("\n")
    }
}
