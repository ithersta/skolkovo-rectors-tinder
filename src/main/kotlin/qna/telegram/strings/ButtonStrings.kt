package qna.telegram.strings

import qna.domain.entities.QuestionIntent

object ButtonStrings {
    object Option {
        const val Yes = "Да"
        const val No = "Нет"
    }

    object Question {
        object Intent {
            const val TestHypothesis = "Хочу проверить гипотезу (посоветоваться)"
            const val Consultation = "У меня есть запрос на консультацию по теме"
            const val FreeForm = "Хочу задать вопрос участникам сообщества (в свободной форме)"
            const val QuestionToColleagues = "Xочу задать вопрос коллегам из Центра трансформации образования"
        }

        val questionIntentToString = mapOf<QuestionIntent, String>(
            QuestionIntent.TestHypothesis to Intent.TestHypothesis,
            QuestionIntent.Consultation to Intent.Consultation,
            QuestionIntent.FreeForm to Intent.FreeForm,
            QuestionIntent.QuestionToColleagues to Intent.QuestionToColleagues
        )
        val stringToQuestionIntent = questionIntentToString.entries.associate { it.value to it.key }
    }

    const val SendQuestion = "Отправить запрос в сообщество"
}
