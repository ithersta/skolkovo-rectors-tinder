package event.telegram

import dev.inmo.tgbotapi.utils.*
import event.domain.entities.Event

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
            "Участникам сообщества отправлено оповещение о новом мероприятии"

        const val EventNotCreated = "❌ Мероприятие не создано"
        const val InvalidTimeInterval = "Некорректно указан временной интервал"
        const val InvalidDataFormat = "Введён неверный формат данных. "
        fun message(event: Event) = buildEntities {
            bold("Название: ")
            regular(event.name)
            regularln("")
            bold("Дата и время начала: ")
            regular(event.timestampBegin.toString())
            regularln("")
            bold("Дата и время окончания: ")
            regular(event.timestampEnd.toString())
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

    object RemoveEvent {
        const val ChooseEvent = "Выберите мероприятие: "
        const val Remove = "Вы действительно хотите удалить мероприятие?"
        const val SuccessfulRemove = "Мероприятие успешно удалено ✅"
        const val NotRemove = "❌ Мероприятие не удалено "
    }

    // TODO придумать как выводить дату и время
//    fun formatInstant(instant: Instant): String {
//        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
//        return formatter.format(instant)
//    }


    fun eventMessage(event: Event) = buildEntities {
        regular("📅 Новое мероприятие  ")
        bold(event.name)
        regularln("🕓 ")
        regular(
            event.timestampBegin.toString() +
                " - " + event.timestampEnd.toString()
        )
        regularln("")
        event.description?.let { regularln(it) }
        regularln("🔗")
        link("Ссылка", event.url)
    }
}
