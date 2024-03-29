package qna.telegram.strings

import qna.domain.entities.QuestionIntent

object ButtonStrings {
    object Question {
        object Intent {
            const val TestHypothesis = "Хочу проверить гипотезу (посоветоваться)"
            const val Consultation = "У меня есть запрос на консультацию по теме"
            const val FreeForm = "Хочу задать вопрос участникам сообщества (в свободной форме)"
            const val QuestionToColleagues = "Xочу задать вопрос коллегам из Центра трансформации образования"
            const val LookingForPartners = "Ищу партнеров для запуска проекта"
        }

        val questionIntentToString = mapOf<QuestionIntent, String>(
            QuestionIntent.TestHypothesis to Intent.TestHypothesis,
            QuestionIntent.Consultation to Intent.Consultation,
            QuestionIntent.FreeForm to Intent.FreeForm,
            QuestionIntent.QuestionToColleagues to Intent.QuestionToColleagues,
            QuestionIntent.LookingForPartners to Intent.LookingForPartners
        )
        val stringToQuestionIntent = questionIntentToString.entries.associate { it.value to it.key }
    }

    object RespondentNoAnswer {
        const val CloseQuestion = "Закрыть вопрос"
        const val SeeList = "Посмотреть список ответивших"
    }

    object SendQuestion {
        const val ToCenter = "Отправить вопрос Центру трансформации образования"
        const val ToAll = "Отправить всем"
        const val ExcludeMyCity = "Исключить участников из моего города"
        const val ExcludeMyOrganization = "Исключить участников из моей организации"
    }

    const val Respond = "Ответить"
}
