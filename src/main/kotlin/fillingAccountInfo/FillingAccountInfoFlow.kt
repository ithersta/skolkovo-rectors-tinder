package fillingAccountInfo

import Strings.AccountInfo.ChooseCity
import Strings.AccountInfo.ChooseProfessionalAreas
import Strings.AccountInfo.WriteOrganization
import Strings.AccountInfo.WriteProfession
import Strings.AccountInfo.WriteProfessionalActivity
import Strings.Question.ChooseQuestionArea
import auth.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import states.*

fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.fillingAccountInfoFlow() {
    state<ChooseCityState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseCity
                // //TODO: здесь внедрить часть Глеба с выбором города из сложного кнопочного меню
            )
        }
        onText {
            // //TODO: здесь внедрить часть Глеба с выбором города из сложного кнопочного меню
            val city = it.content.text // /мб если хранить все города листом, то город учстника хранить не словами, а номером в листе?
            state.override { WriteProfessionState(city) }
        }
    }
    state<WriteProfessionState> {
        onEnter {
            sendTextMessage(
                it,
                WriteProfession
            )
        }
        onText {
            val profession = it.content.text // мб валидация нужна какая-нибудь?
            state.override { WriteOrganizationState(city, profession) }
        }
    }
    state<WriteOrganizationState> {
        onEnter {
            sendTextMessage(
                it,
                WriteOrganization
            )
        }
        onText {
            val organization = it.content.text // мб валидация нужна какая-нибудь?
            state.override { ChooseProfessionalAreasState(city, profession, organization) }
        }
    }
    state<ChooseProfessionalAreasState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseProfessionalAreas
                // //TODO: где-то тут видимо прикол с множественным выбором и видимо обработчиками
                // /!!!!обработчик "Другое" просит ввода и принимает его
            )
            // //после того, как все нужные сферы выбраны, переходим в следующее состояние
            state.override { WriteProfessionalDescriptionState(city, profession, organization, emptyList()) } // пока пусть эмпти будет
        }
    }
    state<WriteProfessionalDescriptionState> {
        onEnter {
            sendTextMessage(
                it,
                WriteProfessionalActivity
            )
        }
        onText {
            val activity = it.content.text // мб валидация нужна какая-нибудь?
            state.override { ChooseQuestionAreasState(city, profession, organization, professionalAreas, activity) }
        }
    }
    state<ChooseQuestionAreasState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseQuestionArea
                // /ну тут видимо опять прикол с множественным выбором инлайн кнопок и обработчиками
            )
        }
        // /создание пользователя в базе данных
        // /ну я думаю, обработчик будет собирать все номера в лист, а потом создавать в базе им штучки
    }
}
