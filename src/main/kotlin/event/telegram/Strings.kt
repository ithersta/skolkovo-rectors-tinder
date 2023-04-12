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

        // TODO тут придумать сообщение
        const val EventIsCreated = "Мероприятие добавлено в календарь ✅ " +
            "Участникам отправлено оповещение о новом мероприятии"

        const val EventNotCreated = "❌ Мероприятие не создано"
        const val InvalidDataFormat = "Введён неверный формат данных. "
        fun message(event: Event) = buildEntities {
            bold("Название: ")
            regular(event.name)
            regularln("")
            boldln("Дата и время начала: ")
            regular(event.timestampBegin.toString())
            regularln("")
            boldln("Дата и время окончания: ")
            regular(event.timestampEnd.toString())
            if (event.description != null) {
                regularln("")
                boldln("Краткое описание: ")
                event.description?.let { regular(it) }
            }
            regularln("")
            boldln("Ссылка: ")
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

    const val New = "📅 Новое мероприятие\n"
    fun eventMessage(event: Event) = buildEntities {
        bold(event.name)
        regularln("🕓 ")
        regular(
            event.timestampBegin.toString() +
                " - " + event.timestampEnd.toString()
        )
        event.description?.let { regularln(it) }
        regularln("🔗")
        link("Подробнее", event.url)
    }
}
